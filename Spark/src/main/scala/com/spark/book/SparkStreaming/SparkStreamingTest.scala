package com.spark.book.SparkStreaming

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object SparkStreamingTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SparkStreamingTest").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc,Milliseconds(5000))  //5000毫秒取一次数据  Seconds(5)等价

    val lines:ReceiverInputDStream[String] = ssc.socketTextStream("hadoop101",8888)  //监控hadoop101机器上的8888端口数据  在机器上nc -l 8888
    val word:DStream[String] = lines.flatMap(_.split(" ")) // (a,b,c,d),(e,f),(g,h,i)
    //现在想得到a,b,c,d,e,f,g,h,i,需要压平用flatMap
    val value:DStream[(String,Int)] = word.map(
      x => {(x,1)}
    )  //得到(a,1),(b,1),(b,1).....
    val res:DStream[(String,Int)] = value.reduceByKey((_+_))
    res.print()//打印结果

    //启动SparkStreaming程序
    ssc.start()
    //等待优雅退出 exit code 0
    ssc.awaitTermination()


  }
}
