package com.study.spark

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object StatStreamingApp {
  def main(args: Array[String]): Unit = {
    // println("My-AiQiYi-Data-SparkStreaming-Project")

    val scc = new StreamingContext("local[*]", "StatStreamingApp", Seconds(5))  //初始化StreamingContext

    //    val conf = new SparkConf().setAppName("StatStreamingApp").setMaster("local[*]")
    //    val sc = new SparkContext(conf)
    //    val scc = new StreamingContext(sc, Seconds(5))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "hadoop101:9092,hadoop102:9092,hadoop103:9092",  //配置kafka机器集群
      "key.deserializer" -> classOf[StringDeserializer]
      ,
      "value.deserializer" -> classOf[StringDeserializer]
      ,
      "group.id" -> "test"
      ,
      "auto.offset.reset" -> "latest"
      ,
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("flumeTopic")  //从flumeTopic消费消息  flumeTopic是生产者
    val stream = KafkaUtils.createDirectStream[String, String](
      scc,  //scc - SparkContext对象
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    ).map(_.value()) //map里的value方法取出对应值 - value返回仅包含值的RDD

    stream.print() //打印
    scc.start()    //开启Streaming
    scc.awaitTermination()  //一直等待数据




  }
}
