package com.spark.book.SparkCore

import org.apache.spark.{SparkConf, SparkContext}
import com.alibaba.fastjson.JSON
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat

/**
  * @Descriptioo:
  * @Owner: jiajing_qu
  * @Date: 2018/06/8 21:14
  * @Version: 1.0
  */
object LearnRDD {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("LearnRDD")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")

    //1.读文件
    println("#################################################################")
    readText(sc)

    //2.读JSON
    println("#################################################################")
    readJson(sc)

    //3.读CSV/TSV文件
    println("#################################################################")
    readCSV_TSV(sc)

    //4.读SequenceFile文件
    println("#################################################################")
    readSequenceFile(sc)

    //5.显式调用Hadoop API读取文件
    println("#################################################################")
    readHDFS(sc)








    sc.stop()
  }

  def readText(sc: SparkContext): Unit = {
    // 读取文件
    val input = sc.textFile("Spark/src/main/scala/com/spark/book/SparkCore/LearnRDD.scala")
    println(input.collect().mkString(","))   // collect行动函数  按行读取，mkString，每条数据用逗号隔开

    val input1 = sc.textFile("Spark/src/main/scala/com/spark/book/SparkCore/*.scala")  // 正则匹配文件读取 读取所有后缀名scala的文件
    println(input1.collect().mkString("\n"))

    val input2 = sc.textFile("Spark/src/main/scala/com/spa*/book/*/*.scala")  // 放大范围，读取所有符合条件的文件  （所有目录都要有访问权限）
    println(input2.collect().mkString(""))
  }

  def readJson(sc: SparkContext): Unit = {
    //读取JSON文本
    val j1 = "{\"name\": \"QJJ\", \"age\": 21, \"addr\":[\"hadoop101:8080\", \"192.168.1.101: 8080\"], \"a\": {1:2}}"
    //fastjson解析json文本
    val json = JSON.parseObject(j1)
    //获取成员
    val name = json.get("name")
    println(name)
    //返回字符串成员
    val nameString = json.getString("name")
    println(nameString)
    //返回整形成员
    val age = json.getInteger("age")
    println(age)
    //返回多级成员
    val a = json.getJSONObject("a").get(1)
    println(a)

    //读取JSON文件
    val inputJson = sc.textFile("Spark/src/main/scala/com/spark/book/SparkCore/jsonFile")
    import scala.util.parsing.json.JSON
    val content = inputJson.map(JSON.parseFull)
    println(content.collect().mkString(","))
  }

  def readCSV_TSV(sc: SparkContext): Unit = {
    //flatMap和foreach使用
    //CSV读取
    val inputCSV = sc.textFile("Spark/src/main/scala/com/spark/book/SparkCore/CSV_File").flatMap(_.split(",")).collect()
    inputCSV.foreach(println)

    //TSV读取
    val inputTSV = sc.textFile("Spark/src/main/scala/com/spark/book/SparkCore/TSV_File").flatMap(_.split("\t")).collect()
    inputTSV.foreach(println)
  }

  //SequenceFile读取
  def readSequenceFile(sc: SparkContext): Unit = {
    val inputSequence = sc.sequenceFile[Int,String]("Spark/src/main/scala/com/spark/book/SparkCore/Sequence_File")
    println(inputSequence.collect().mkString(","))
  }

  //显式调用HadoopAPI读取
  def readHDFS(sc: SparkContext): Unit = {
    val path = "hdfs://localhost:9000/datas/spark_test/file_in_hdfs.txt"
    val inputHDFSFile = sc.newAPIHadoopFile[LongWritable,Text,TextInputFormat](path)  // [LongWritable,Text,TextInputFormat]是泛型，第一个LongWritable是每一行的偏移量，第二个Text是内容，第三个TextInputFormat是指读取数据的InputFormat类型
    val res = inputHDFSFile.map(_._2.toString).collect()   //inputHDFSFile不能直接mkString 此时inputHDFSFile类型是RDD[(LongWritable，Text)]  Text转为String才能输出
//    val res = inputHDFSFile.map(t=>t._2.toString).collect()  这种写法也可以
    println(res.mkString("\n"))
  }


}
