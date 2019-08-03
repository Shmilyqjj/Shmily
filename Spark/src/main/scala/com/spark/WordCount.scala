package com.spark

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    //创建SparkConf()并设置APP名称
    val conf = new SparkConf().setAppName("wordcount").setMaster("local[2]")
    //创建SparkContext，该对象提交spark app入口
    val sc = new SparkContext(conf)

    ////使用sc创建RDD并执行相应的transformation和action
    sc.textFile(args(0)).flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_,1).sortBy(_._2,false).saveAsTextFile(args(1))

    //停止sc，结束任务
    sc.stop();
  }
}

//打jar包提交集群运行，arg0是输入文件，arg1是输出路径