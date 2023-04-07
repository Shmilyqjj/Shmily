package com.spark.redis
import com.redislabs.provider.redis._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hive.metastore.api.Table
import org.apache.iceberg.catalog.TableIdentifier
import org.apache.iceberg.spark.SparkCatalog
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{IntegerType, LongType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}



object SparkRedis {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("sparkRedis")
    conf.set("spark.redis.host", "127.0.0.1")    //redis host,随便一个节点，自动发现
    conf.set("spark.redis.port", "6379")  // 端口号，不填默认为6379
    conf.set("spark.redis.auth","123456")  // 密码
    conf.set("spark.redis.db","0")  // 数据库设置
    conf.set("spark.redis.timeout","2000")  //设置连接超时时间

    // Iceberg相关设置
    conf.set("spark.sql.sources.partitionOverwriteMode", "dynamic")
//    conf.set("spark.sql.catalog.spark_catalog", "org.apache.iceberg.spark.SparkSessionCatalog")
//    conf.set("spark.sql.catalog.spark_catalog.type", "hive")


    conf.set("spark.sql.catalog.spark_catalog", "org.apache.iceberg.spark.SparkCatalog")
    conf.set("spark.sql.catalog.spark_catalog.type", "hive")
    conf.set("spark.sql.extensions", "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions")
    conf.set("spark.sql.catalog.spark_catalog.warehouse", "hdfs://shmily:8020/user/hive/warehouse")
    conf.set("hive.metastore.uris", "thrift://shmily:9083")
    conf.set("spark.sql.warehouse.dir", "hdfs://shmily:8020/user/hive/warehouse")



    //    conf.set("spark.sql.catalog.local", "org.apache.iceberg.spark.SparkCatalog")
    //    conf.set("spark.sql.catalog.local.type", "hive")
    //    conf.set("spark.sql.catalog.local.warehouse", "hdfs://shmily:8020/user/hive/warehouse")


    val spark: SparkSession = SparkSession.builder.master("local").appName("SparkRedis").config(conf).enableHiveSupport().getOrCreate()

    val hadoopConf = new Configuration
    for (sourceFile <- "/etc/hadoop-conf/core-site.xml,/etc/hadoop-conf/hdfs-site.xml".split(",")) {
      hadoopConf.addResource(new Path(sourceFile))
    }
    setKerberos(hadoopConf,"/etc/krb5.conf", "/opt/keytabs/hdfs.keytab", "hdfs")

//    val resultSet:RDD[(String, String)] = sc.fromRedisKV("test*")
    val resultSet:RDD[(String, String)] = spark.sparkContext.fromRedisHash("t:20230221")

    val structSchema: StructType = StructType(
      List(
        StructField("bizid", StringType, true),
        StructField("actionid", StringType, true),
        StructField("success_cnt", LongType, true),
        StructField("failed_cnt", LongType, true),
        StructField("total_success_cnt", LongType, true),
        StructField("total_failed_cnt", LongType, true),
        StructField("total_cnt", LongType, true)
      )
    )

    val structRow: RDD[Row] = resultSet.map(line =>{
      val rk: Array[String] = line._1.trim.split(":")
      val rv: Long = java.lang.Long.parseLong(line._2.trim)
      rk(0) match {
        case "b" => Row(rk(1), null, null, null, null, null ,rv)
        case "ba" => Row(rk(1),rk(2),null,null,null,null,rv)
        case "bas" => Row(rk(1), rk(2), rv, null, null, null, null)
        case "baf" => Row(rk(1), rk(2), null, rv, null, null, null)
        case "bs" => Row(rk(1), null, rv, null, null, null, null)
        case "bf" => Row(rk(1), null, null, rv, null, null, null)
        case "t" => Row(null, null, null, null, null, null, rv)
        case "ts" => Row(null, null, null, null, rv, null, null)
        case "tf" => Row(null, null, null, null, null, rv, null)
      }
    })

    //创建dataframe
    val df: DataFrame = spark.createDataFrame(structRow,structSchema)
    df.printSchema()
//    df.show()
    df.createTempView("redis_table")

//    spark.sql("use spark_catalog")
    spark.sql("show databases").show()
    spark.sql("show tables").show()
    spark.sql("select * from iceberg_db.redis_table").show()



//    df.writeTo("iceberg_db.spark_to_redis").using("iceberg").createOrReplace()

//    spark.sql("select * from iceberg_db.spark_to_redis").show(100)









//    df.write
//      .format("org.apache.spark.sql.redis")
//      .option("table", "foo")
//      .save()

  }

  def setKerberos(conf: Configuration, krb5Path: String,keytabPath: String, loginUser: String) : Unit = {
    System.setProperty("java.security.krb5.conf", krb5Path)
    conf.setBoolean("hadoop.security.authentication", true)
    conf.set("hadoop.security.authentication", "Kerberos")
    import org.apache.hadoop.security.UserGroupInformation
    UserGroupInformation.setConfiguration(conf)
    UserGroupInformation.loginUserFromKeytab(loginUser, keytabPath)
  }

}
