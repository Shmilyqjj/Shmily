package top.qjj.shmily.kudu.app

import org.apache.commons.codec.StringDecoder
import org.apache.spark.sql.streaming.{ProcessingTime, StreamingQueryListener, StreamingQueryProgress}
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.streaming.dstream.{DStream, InputDStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.parallel.immutable

/**
 * :Description: 消费Kafka写Kudu  可以用于数据同步时Kudu数据同步双写
 * :Author: 佳境Shmily
 * :Create Time: 2021/8/21 14:42
 * :Site: shmily-qjj.top
 * 程序运行参数--packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.0
 *
 * 1.Create a Kafka topic
 * kafka-topics --create --zookeeper cdh101:2181 --replication-factor 1 --partitions 1 --topic kudu_data_topic
 * kafka-console-producer --broker-list cdh101:9092 --topic kudu_data_topic
 * kafka-console-consumer --bootstrap-server cdh101:9092 --topic kudu_data_topic
 *
 * 2.Send data to kafka
 *
 * 3. check kudu table schema and data
 *
 */
object Kafka2Kudu {
  def main(args: Array[String]): Unit = {
//    val ssc = getSparkStreamingContext("yarn")
//    //设置checkpoint
//    ssc.checkpoint("./Kafka_Receiver")
//    //配置kafka相关参数
//    val kafkaParams=Map("metadata.broker.list"->"cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092","group.id"->"KuduCG", "serializer.class"->"kafka.serializer.StringDecoder")
//    //定义topic
//    val topics=Set("kudu_data_topic")
//    val dstream: InputDStream[(String, String)] = KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder](ssc,kafkaParams,topics)
//    val topicData: DStream[String] = dstream.map(_._2)
//    val wordAndOne: DStream[(String, Int)] = topicData.flatMap(_.split(" ")).map((_,1))
//    //相同单词出现的次数累加
//    val result: DStream[(String, Int)] = wordAndOne.reduceByKey(_+_)
//    result.print()
//    //开启计算
//    ssc.start()
//    ssc.awaitTermination()

    val spark = getSparkContext("local[4]")
    val kuduMasterAddrs = "cdh102,cdh103,cdh104"


    var batchId: Long = 0
    //对查询添加一个监听，获取每个批次的处理信息 监控Streaming
    //通过异步的方式对实时流进行监控，输出每次batch触发从Kafka获取的起始和终止的offset，总条数，以及通过最大Event-Time计算得到的Watermark等
    spark.streams.addListener(new StreamingQueryListener() {
      override def onQueryStarted(event: StreamingQueryListener.QueryStartedEvent): Unit = {}
      override def onQueryProgress(event: StreamingQueryListener.QueryProgressEvent): Unit = {
        val progress: StreamingQueryProgress = event.progress
        batchId = progress.batchId
        val inputRowsPerSecond: Double = progress.inputRowsPerSecond
        val processRowsPerSecond: Double = progress.processedRowsPerSecond
        val numInputRows: Long = progress.numInputRows
        println("batchId=" + batchId, "  numInputRows=" + numInputRows + "  inputRowsPerSecond=" + inputRowsPerSecond +
          "  processRowsPerSecond=" + processRowsPerSecond)
      }
      override def onQueryTerminated(event: StreamingQueryListener.QueryTerminatedEvent): Unit = {}
    })

    val topic:String = "kudu_data_topic"
    //Spark StructStreaming
    val StreamingDF:DataFrame = spark.readStream.format("kafka")
      .option("kafka.bootstrap.servers", "cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092")
      .option("startingOffsets", "latest")
      .option("maxOffsetsPerTrigger", "10000")
      .option("subscribe", topic)
      .option("failOnDataLoss", true)
      .load()
//    StreamingDF.printSchema()

    // 输出消费到的数据到console
//    val query = StreamingDF.selectExpr("CAST(value AS STRING)")
//      .writeStream.outputMode("update")
//      .format("console")
//      .trigger(ProcessingTime("10 seconds")).start()

    val query = StreamingDF.selectExpr("CAST(value AS STRING)")
      .writeStream
      .foreach(new KuduSink(kuduMasterAddrs))
      .outputMode("update")
      .trigger(ProcessingTime("10 seconds"))
      .start()
    query.awaitTermination()
  }

  def getSparkStreamingContext(master:String, appName:String="Kafka2Kudu"):StreamingContext = {
    val conf = new org.apache.spark.SparkConf()
      .setMaster(master)
      .setAppName(appName)
      .set("spark.streaming.receiver.writeAheadLog.enable","true")
    val sc = new org.apache.spark.SparkContext(conf)
    sc.setLogLevel("WARN")
    new StreamingContext(sc,Seconds(5))
  }

  def getSparkContext(master:String, appName:String="Kafka2Kudu"):SQLContext = {
    val conf = new org.apache.spark.SparkConf()
      .setMaster(master)
      .setAppName(appName)
    val sc = new org.apache.spark.SparkContext(conf)
    sc.setLogLevel("WARN")
    new SQLContext(sc)
  }
}
