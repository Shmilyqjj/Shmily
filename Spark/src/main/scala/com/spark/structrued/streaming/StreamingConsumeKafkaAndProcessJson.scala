package com.spark.structrued.streaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.ProcessingTime
import org.apache.spark.sql.types.StructType

/**
 * :Description: Structured Streaming消费Kafka 处理json数据
 * :Author: 佳境Shmily
 * :Create Time: 2021/11/26 21:53
 * :Site: shmily-qjj.top
 * 运行时需要指定Program Arguments： --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.0
 */
object StreamingConsumeKafkaAndProcessJson {
  private val BOOTSTRAP_SERVERS = "192.168.1.101:9092,192.168.1.102:9092,192.168.1.103:9092,192.168.1.104:9092"
  private val TOPIC = "test_topic"

  def main(args: Array[String]): Unit = {
    val spark:SparkSession = SparkSession.builder
      .master("local[4]")
      .appName("StreamingConsumeKafkaAndProcessJson")
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    val stream_data = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
    .option("subscribe", TOPIC)
    .option("startingOffsets", "latest")
    .option("maxOffsetsPerTrigger", "10000")  //控制断点续传一个batch最大处理条数，避免一次处理太多任务导致内存溢出
    .option("failOnDataLoss", true)
    .load()
    stream_data.printSchema()
    /**
     * root
     * |-- key: binary (nullable = true)
     * |-- value: binary (nullable = true)
     * |-- topic: string (nullable = true)
     * |-- partition: integer (nullable = true)
     * |-- offset: long (nullable = true)
     * |-- timestamp: timestamp (nullable = true)
     * |-- timestampType: integer (nullable = true)
     */

    val query = stream_data
      .selectExpr("concat(topic,'-',partition,'-',offset) as kafka_topic_partition_offset", "get_json_object(cast(value as string),'$.msg') as msg", "get_json_object(cast(value as string),'$.count') as count")
      .writeStream
      .format("console")
      .trigger(ProcessingTime("10 seconds"))
      .start()

    query.awaitTermination()
  }
}
