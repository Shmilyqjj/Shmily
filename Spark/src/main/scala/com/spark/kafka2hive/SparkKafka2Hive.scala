package com.spark.kafka2hive

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.kafka.clients.admin.{AdminClient, AlterConsumerGroupOffsetsOptions, ListConsumerGroupOffsetsOptions, OffsetSpec}
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.util
import java.util.Properties
import scala.collection.JavaConverters._
import scala.collection.Seq
import scala.collection.convert.ImplicitConversions.`map AsScala`

/**
 bin/spark-submit \
 --class com.spark.kafka2hive.SparkKafka2Hive \
 --master local \
 --deploy-mode client \
 /mnt/nas/seatunnel/Spark-1.0-SNAPSHOT.jar \
 --appName kafka2hive \
 --broker "localhost:9092" \
 --topic "test_topic" \
 --group "test_group" \
 --hiveTable "test.test_kafka2hive_content_dd" \
 --hiveMetastoreUris "thrift://shmily:9083" \
 --dayPartitionCol ds \
 --valueCol message \
 --overwrite false
 如果机器已经kinit则无需--kerberosEnabled，--krb5Conf，--principal，--keytab
 Spark的jars目录需要添加如下jar: spark-sql-kafka-0-10_2.12-3.5.6.jar commons-pool2-2.11.1.jar spark-token-provider-kafka-0-10_2.12-3.5.6.jar


 */

object SparkKafka2Hive {
  def main(args: Array[String]): Unit = {
    // 0. parse args
    val argMap = new util.HashMap[String, String]()
    args.sliding(2, 2).foreach {
      case Array(k, v) => argMap.put(k, v)
    }

    // 获取所有参数值，带默认值
    val appName: String = argMap.getOrElse("--appName", "Kafka2HiveTask")
    val sparkMaster: String = argMap.getOrElse("--sparkMaster", "local[*]")
    val broker: String = argMap.getOrElse("--broker", "localhost:9092")
    val topic: String = argMap.getOrElse("--topic", "seatunnel")
    val group: String = argMap.getOrElse("--group", "test_group")
    val hiveTable: String = argMap.getOrElse("--hiveTable", "default.test_table")
    val hiveMetastoreUris: String = argMap.getOrElse("--hiveMetastoreUris", "thrift://shmily:9083")
    val dayPartitionCol: String = argMap.getOrElse("--dayPartitionCol", "ds")
    val valueCol: String = argMap.getOrElse("--valueCol", "message")
    val overwrite: Boolean = argMap.getOrDefault("--overwrite", "false").equals("true")
    val kerberosEnabled: Boolean = argMap.getOrDefault("--kerberosEnabled", "false").equals("true")
    val krb5Conf: String = argMap.getOrElse("--krb5Conf", "/etc/krb5.conf")
    val principal: String = argMap.getOrElse("--principal", "admin/admin@SHMILY-QJJ.TOP")
    val keytab: String = argMap.getOrElse("--keytab", "/opt/modules/keytabs/admin.keytab")


    // 1. kafka admin client init
    val adminClient = getKafkaAdminClient(broker)

    // 2. get start group offsets
    val startOffsets: Map[String, Long] = getStartingOffsets(adminClient, topic, group)
    val endOffsets: Map[String, Long] = getLatestOffsets(adminClient, topic)
    val startingOffsets: String = offsetMap2Json(topic, startOffsets)
    val endingOffsets: String = offsetMap2Json(topic, endOffsets)
    println(s"Topic=$topic Group=$group")
    println(s"startingOffsets=$startingOffsets")
    println(s"endingOffsets=$endingOffsets")
    if (startingOffsets == endingOffsets) {
      println("start and end offsets are the same, no message was consumed. exit program.")
      System.exit(0)
    }

    // 3. hadoop and krb init
    if (kerberosEnabled) {
      val hadoopConf = new Configuration
      for (sourceFile <- "/opt/modules/hadoop/etc/hadoop/core-site.xml,/opt/modules/hadoop/etc/hadoop/hdfs-site.xml".split(",")) {
        hadoopConf.addResource(new Path(sourceFile))
      }
      setKerberos(hadoopConf,krb5Conf, keytab, principal)
    }

    // 4. spark session init
    val spark = SparkSession.builder()
      .appName(appName)
      .master(sparkMaster)
      .config("spark.hadoop.hadoop.security.authentication", "kerberos")
      .config("hive.server2.authentication", "kerberos").config("spark.hadoop.hadoop.security.krb5.debug", "true")
//      .config("spark.kerberos.keytab", keytab)
//      .config("spark.kerberos.principal", principal)
//      .config("spark.hadoop.dfs.namenode.kerberos.principal", "hdfs/shmily@SHMILY-QJJ.TOP")
//      .config("spark.hadoop.dfs.datanode.kerberos.principal", "hdfs/shmily@SHMILY-QJJ.TOP")
//      .config("spark.hadoop.fs.defaultFS", "hdfs://shmily:8020")
      .config("spark.hadoop.hive.exec.dynamic.partition", "true")
      .config("spark.hadoop.hive.exec.dynamic.partition.mode", "nonstrict")
      .config("spark.sql.sources.partitionOverwriteMode", "dynamic")
      .config("spark.sql.mapKeyDedupPolicy", "LAST_WIN")
      .config("spark.hive.metastore.sasl.enabled", "true")
      .config("spark.hadoop.hive.metastore.uris", hiveMetastoreUris)
//      .config("spark.hadoop.hive.server2.authentication", "KERBEROS")
//      .config("hive.metastore.uris", "thrift://shmily:9083")
//      .config("hive.metastore.kerberos.principal", "hive/shmily@SHMILY-QJJ.TOP")
//      .config("hive.metastore.kerberos.keytab", "/opt/modules/keytabs/hive.keytab")
//      .config("spark.sql.warehouse.dir", "hdfs://shmily:8020/user/hive/warehouse")
      .enableHiveSupport()
      .getOrCreate()



    // 5. kafka source init
    val lines: DataFrame = spark.read
      .format("kafka")
      .option("kafka.bootstrap.servers", broker)
      .option("subscribe", topic)
      .option("startingOffsets", startingOffsets)
      .option("endingOffsets", endingOffsets)
      .load
    lines.createTempView("kafka_source_table")

    // 5. data transform
    val sinkData = spark.sql(s"select date_format(timestamp,'yyyyMMdd') as $dayPartitionCol, cast(value as string) as $valueCol from kafka_source_table")

//    spark.sql("desc default.test_table").show()

    // 6. sink hive
    sinkData.write
      .partitionBy(dayPartitionCol)
      .mode(if(overwrite) SaveMode.Overwrite else SaveMode.Append)
      .saveAsTable(hiveTable)
    sinkData.show()


    // 7. commit group offset
    commitOffsets(adminClient, topic, group, endOffsets)

    // 8. clear
    spark.stop()
  }

  private def setKerberos(conf: Configuration, krb5Path: String,keytabPath: String, loginUser: String) : Unit = {
    System.setProperty("java.security.krb5.conf", krb5Path)
    System.setProperty("sun.security.krb5.debug", "false")
    conf.setBoolean("hadoop.security.authorization", true)
    conf.set("hadoop.security.authentication", "kerberos")
    import org.apache.hadoop.security.UserGroupInformation
    UserGroupInformation.setConfiguration(conf)
    UserGroupInformation.loginUserFromKeytab(loginUser, keytabPath)
    println(s"kerberos login finished, user:$loginUser")
  }

  private def getKafkaAdminClient(broker: String): AdminClient = {
    val props: Properties = new Properties
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker)
    AdminClient.create(props)
  }

  def getStartingOffsets(adminClient: AdminClient, topic: String, groupId: String): Map[String, Long] = {
    val topicDesc = adminClient.describeTopics(List(topic).asJava).values().get(topic).get()
    val topicPartitions: java.util.List[TopicPartition] = topicDesc.partitions().asScala
      .map(tp => new TopicPartition(topic, tp.partition()))
      .toList.asJava
    val offsets = adminClient
      .listConsumerGroupOffsets(groupId, new ListConsumerGroupOffsetsOptions().topicPartitions(topicPartitions))
      .partitionsToOffsetAndMetadata()
      .get()
    val groupOffsets: Map[String, Long] = Option(offsets)
      .map(_.asScala)
      .getOrElse(Seq.empty)
      .flatMap {
        case (tp, offsetMeta) =>
          Option(tp).flatMap(t =>
            Option(offsetMeta).map(m => (t.partition().toString, m.offset()))
          )
      }
      .toMap
    groupOffsets
  }

  private def getLatestOffsets(adminClient: AdminClient, topic: String): Map[String, Long] = {
    val topicDesc = adminClient.describeTopics(List(topic).asJava).values().get(topic).get()
    val topicPartitions: java.util.List[TopicPartition] = topicDesc.partitions().asScala
      .map(tp => new TopicPartition(topic, tp.partition()))
      .toList.asJava
    val offsetSpec = OffsetSpec.latest()
    val offsetsFuture = adminClient.listOffsets(
      topicPartitions.asScala.map(tp => tp -> offsetSpec).toMap.asJava
    )
    val offsets = offsetsFuture.all().get()
    val latestOffsets = offsets.asScala.map { case (tp, offsetInfo) =>
      (tp.partition().toString, offsetInfo.offset())
    }.toMap
    latestOffsets
  }

  private def commitOffsets(adminClient: AdminClient, topic: String, group: String, endOffsets:Map[String, Long]): Unit = {
    val offsets: Map[TopicPartition, OffsetAndMetadata] = endOffsets.map(
      p => new TopicPartition(topic, p._1.toInt) -> new OffsetAndMetadata(p._2)
    )
    val options = new AlterConsumerGroupOffsetsOptions().timeoutMs(5000)
    adminClient.alterConsumerGroupOffsets(group, offsets.asJava, options).all().get()
    println(s"Commited partition offsets = $endOffsets")
    adminClient.close()
  }

  private def offsetMap2Json(topic: String, offsets: Map[String, Long]): String = {
    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    s"""{"$topic":${write(offsets)}}"""
  }


}
