package com.spark.book.SparkCore

/**
*Description：  Spark WordCount例子
*Owner: jiajing_qu
*Date:  2018.6
*/

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    //初始化SparkConf对象，设置参数  setMaster可以改成spark://hadoop101:7077 改为Spark集群模式运行
    val conf = new SparkConf().setMaster("local[*]").setAppName("MyWordCount")   //设置目标Master的地址  设置任务名  local[*]本地模式

    //实例化SparkContext
    val sc = new SparkContext(conf)

    //读取文件并WordCount
    sc.textFile("hdfs://localhost:9000/Data/TestData/a.txt").flatMap(_.split(" ")).map((_,1)).reduceByKey(_ + _, 1).sortBy(_._2, false).saveAsTextFile("hdfs://localhost:9000/Data/TestData/WordCount_out")
    //结果
//    (CCI,14)
//    (spark,9)
//    (hadoop,9)
//    (word,8)
//    (hello,8)
//    (count,8)
//    (world,8)
//    (Ha,2)

    sc.stop()
  }
}

/**
* 打包 提交运行
bin/spark-submit \
--class com.spark.book.SparkCore.WordCount \
--master spark://hadoop101:7077 \
--executor-memory 1G \
--total-executor-cores 3 \
/home/jiajing_qu/JarFile-1.0.jar

*/
