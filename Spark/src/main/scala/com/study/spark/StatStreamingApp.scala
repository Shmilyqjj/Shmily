package com.study.spark

/**
  * 爱奇艺视频数据分析可视化项目 Kafka连接Spark部分
  */
import org.apache.spark.streaming.{Seconds, StreamingContext}
import com.study.spark.util.{CategorySearchClickCount, _}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import scala.collection.mutable.ListBuffer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.kafka.clients.consumer.ConsumerRecord

object StatStreamingApp {
  def main(args: Array[String]): Unit = {
    // println("My-AiQiYi-Data-SparkStreaming-Project")

    val scc = new StreamingContext("local[2]", "StatStreamingApp", Seconds(5))  //初始化StreamingContext

//        val conf = new SparkConf().setAppName("StatStreamingApp").setMaster("local[*]")
//        val sc = new SparkContext(conf)
//        val scc = new StreamingContext(sc, Seconds(5))

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
    val logs = KafkaUtils.createDirectStream[String, String](
      scc,  //scc - SparkContext对象
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    ).map(_.value()) //map里的value方法取出对应值 - value返回仅包含值的RDD

//    logs.print() //流式输出logs到控制台
    //156.167.29.143  2019-08-04 02:11:36     "GET www/3 HTTP/1.0"    -       404
    //29.143.187.100  2019-08-04 02:11:36     "GET tuokouxiu/821 HTTP/1.0"    https://www.sougou.com/web?k=GitHub    200
    //30.143.187.10   2019-08-04 02:11:36     "GET tuokouxiu/821 HTTP/1.0"    -       200

    var cleanLog = logs.map(line=>{
      var infos = line.split("\t")
      var strs = infos(2).split(" ")  //infos(2) 是网址那段，对网址按空格再进行切分
      var url = strs(1) //取出www/3  或者 tuokouxiu/821 第二段字段
      var categoryId = 0
      if(url.startsWith("www")){
        categoryId = url.split("/")(1).toInt  //取出代表类别的字段（1，2，3，4，5，6）  - categoryId
      }
      ClickLog(infos(0),DataUtils.parseToMin(infos(1)),categoryId,infos(3),infos(4).toInt)
    }).filter(log=>log.categoryId!=0)  //过滤掉脏数据日志  category=0就是脏数据

    //cleanLog.print() //-至此，日志过滤完成 得到cleanLog

    //统计每个类别每天的点击量 需要cleanLog里得day和CategoryId  得到day_CategoryId
    cleanLog.map(log=>{
      (log.time.substring(0,8)+"_"+log.categoryId,1)  //拼接 date+categoryId 1
    }).reduceByKey(_+_).foreachRDD(rdd=>{   //相同key累加
      rdd.foreachPartition(partitions=>{
        val list = new ListBuffer[CategoryClickCount]
        partitions.foreach(pair=>{  //遍历partition中的每一个
          list.append(CategoryClickCount(pair._1,pair._2))
        })
        CategoryClickCountDAO.save(list)
      })
    })


    //每个栏目从什么来源/渠道获得的流量多 （日期+渠道+类别） 20190607_refer_categoryId 点击量
    //先创建数据库category_searchcount  语句：create 'category_searchcount','info'
    //rowkey设计 day_refer_categoryId  值：count数量
    cleanLog.map(log=>{
      //29.143.187.100  2019-08-04 02:11:36     "GET tuokouxiu/821 HTTP/1.0"    https://www.sougou.com/web?k=GitHub    200
      val url = log.refer.replace("//","/")  // 将//转为/便于分割
      val splits = url.split("/")  //得到来源网站的URL按/分割 得到的数组splits
      var host = ""  //注意var可以被赋值而val不可以  val是不可变的不可修改
      if(splits.length>2){
        host = splits(1)
      }
      (host,log.time,log.categoryId)        //拼接host（网站来源）+日志时间+日志的类别ID
    }).filter(x=>x._1 != "").map(x=>{
      (x._2.substring(0,8)+"_"+x._1+"_"+x._3,1)  //将时间_host来源_类别ID,计数1  拼接
    }).reduceByKey(_+_).foreachRDD(rdd=>{
      rdd.foreachPartition(partitions=>{
        val list = new ListBuffer[CategorySearchClickCount]
        partitions.foreach(pairs=>{
          list.append(CategorySearchClickCount(pairs._1,pairs._2))
        })
        CategorySearchClickCountDAO.save(list)
      })
    })



    scc.start()    //开启Streaming
    scc.awaitTermination()  //一直等待数据




  }
}
