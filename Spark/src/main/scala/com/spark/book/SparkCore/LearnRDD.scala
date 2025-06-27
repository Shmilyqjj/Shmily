package com.spark.book.SparkCore

import java.sql.DriverManager

import org.apache.spark.{SparkConf, SparkContext}
import com.alibaba.fastjson.JSON
import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.rdd.RDD

//import scala.util.parsing.json.{JSONArray, JSONObject}

/**
  * @Description:  Spark RDD读写基本操作学习 scala版本
  * @Owner: jiajing_qu
  * @Date: 2018/06/8 21:14
  * @Version: 1.0
  */
object LearnRDD {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("LearnRDD")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
//    readAll(sc)        //所有读取操作
    println("\nxxxxxxxxxxxxxxxxxxxxxxx以上是读取操作，以下是写入操作xxxxxxxxxxxxxxxxxxxxxxxxx\n")
//    writeAll(sc)      //所有写入操作

    sc.stop()
  }

  def readAll(sc: SparkContext): Unit = {
    //所有读取操作
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

    //5.读SequenceFile文件
    println("#################################################################")
    readObjectFile(sc)

    //6.显式调用Hadoop API读取文件
    println("#################################################################")
    readHDFS(sc)

    //7.读取Mysql
    println("#################################################################")
    readMysql(sc)
  }

  def writeAll(sc: SparkContext): Unit = {
    //所有写入操作
    val rdd = getRDD(sc)
    //写RDD内容到文本文件
    writeToText(rdd)

    //写出Json文件
    writeToJson(sc)

    //写入CSV/TSV文件
    writeToCSVTSV(sc)

    //写入SequenceFile
    writeToSequenceFile(sc)

    //写入Object文件
    writeToObject(sc)

    //写入Hadoop文件
    writeToHDFSFile(sc)

    //写入Mysql
    writeToMysql(sc)
  }

/** ############################################################################################################ */

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
//    val inputJson = sc.textFile("Spark/src/main/scala/com/spark/book/SparkCore/Json_File")
//    import scala.util.parsing.json.JSON
//    val content = inputJson.map(JSON.parseFull)
//    println(content.collect().mkString(","))
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

  //Object文件读取
  def readObjectFile(sc: SparkContext): Unit = {
    val inputObject = sc.objectFile[Person]("Spark/src/main/scala/com/spark/book/SparkCore/Object_File")
    println(inputObject.collect.toList)
  }

  //显式调用HadoopAPI读取
  def readHDFS(sc: SparkContext): Unit = {
    val path = "hdfs://localhost:9000/datas/spark_test/file_in_hdfs.txt"
    val inputHDFSFile = sc.newAPIHadoopFile[LongWritable,Text,TextInputFormat](path)  // [LongWritable,Text,TextInputFormat]是泛型，第一个LongWritable是每一行的偏移量，第二个Text是内容，第三个TextInputFormat是指读取数据的InputFormat类型
    val res = inputHDFSFile.map(_._2.toString).collect()   //inputHDFSFile不能直接mkString 此时inputHDFSFile类型是RDD[(LongWritable，Text)]  Text转为String才能输出
//    val res = inputHDFSFile.map(t=>t._2.toString).collect()  这种写法也可以
    println(res.mkString("\n"))
  }

  def readMysql(sc: SparkContext): Unit = {
    //需要mysql jdbc jar
    val readMysqlRDD = new JdbcRDD(sc,() => {
      Class.forName("com.mysql.cj.jdbc.Driver")
      DriverManager.getConnection("jdbc:mysql://localhost:3306/student?characterEncoding=utf8", "root", "123456")
    },"select * from grades where class >= ? and class <= ?;",1,8,1,
      r => (r.getString(1),r.getInt(2),r.getInt(3)))
    println("条数:" + readMysqlRDD.count())
    readMysqlRDD.foreach(println)
  }

  //通过parallelize方法生成RDD
  def getRDD(sc:SparkContext): RDD[(String,Int)] = sc.parallelize(Array(("One",1),("Two",2),("Three",3)),6)  //获取RDD,分片数6  Scala数组转为RDD
  def getRDDTest(sc:SparkContext): RDD[(String,Int)] = sc.parallelize(Array(("One",1),("Two",2),("Three",3)))  //获取RDD,分片数由Spark默认分配

  //写RDD内容到文本文件
  def writeToText(rdd: RDD[(String,Int)]): Unit = {
    //合理设置RDD分片数， RDD分片数就是输出文件的个数 如果RDD只有三个元素却有6个分片，则三个元素会分别输出到三个文件，另外三个为空文件
    val path = "D:\\tmp\\tmp1"
    rdd.saveAsTextFile(path)
    println("写RDD内容到文本文件到%s成功".format(path))
  }

  //写出JSON内容
  def writeToJson(sc:SparkContext): Unit = {
    val path = "D:\\tmp\\tmp2"
//    val map1 = Map("name" -> "qjj","age" -> "21", "desc" -> JSONArray(List("weight","sex")))
//    val map2 = Map("name" -> "zxw","age" -> "21", "desc" -> JSONArray(List("weight","sex")))
//    val rdd = sc.parallelize(List(JSONObject(map1),JSONObject(map2)),1)
//    rdd.saveAsTextFile(path)
//    println("写出JSON内容到%s成功".format(path))
  }

  //写入CSV/TSV文件
  def writeToCSVTSV(sc:SparkContext): Unit={
    val CSVpath = "D:\\tmp\\tmp3"
    val TSVpath = "D:\\tmp\\tmp4"
    val array = Array("qjj","21","zxw","22","gjj","24") //数据源，一个数组
    //存CSV
    val CSVRDD = sc.parallelize(Array(array.mkString(",")), 1) //转换array到csv
    CSVRDD.saveAsTextFile(CSVpath)
    println("写出CSV格式到%s成功".format(CSVpath))

    //存TSV
    val TSVRDD = sc.parallelize(Array(array.mkString("\t")), 1) //转换array到csv
    TSVRDD.saveAsTextFile(TSVpath)
    println("写出TSV格式到%s成功".format(TSVpath))
  }

  //写入SequenceFile
  def writeToSequenceFile(sc:SparkContext): Unit = {
    val path:String = "D:\\tmp\\tmp5"
    val rdd = sc.parallelize(List(("qjj",21),("zxw",21)))
    rdd.saveAsSequenceFile(path)
    println("写入SequenceFile到%s成功".format(path))
  }

  //写入Object文件
  def writeToObject(sc:SparkContext): Unit = {
    val path:String = "D:\\tmp\\tmp6"
    val p1 = Person("qjj",21)
    val p2 = Person("zxw",21)
    val rdd = sc.parallelize(List(p1,p2),1)
    rdd.saveAsObjectFile(path)
    println("写入Object文件到%s成功".format(path))
  }

  //写入Hadoop文件
  def writeToHDFSFile(sc:SparkContext):Unit = {
    val rdd = sc.parallelize(List(("qjj",21),("zxw",21),("gjj",24)),1)
    val hdfsPath = "hdfs://localhost:9000/datas/spark_test/outputs/writedFiles"
    rdd.saveAsNewAPIHadoopFile(hdfsPath,classOf[Text],classOf[IntWritable],classOf[TextOutputFormat[Text,IntWritable]])
    println("写入HDFS文件到%s成功".format(hdfsPath))
  }

  //写RDD数据到Mysql  Mysql不适合高并发写入，如果需要实现SQL操作，可以使用Phoenix基于HBase来写入
  def writeToMysql(sc:SparkContext):Unit ={
    val rdd = sc.parallelize(List((1,"110"),(2,"120")))
    Class.forName("com.mysql.cj.jdbc.Driver")

    rdd.foreachPartition((iter: Iterator[(Int,String)]) => {  //foreachPartition遍历rdd的每个分区
      val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student?characterEncoding=utf8","root","123456")   //实例化Connection放在foreachPartition外会报异常  prepareStatement对象和Connection对象不能跨分区实例化（因为多个分区，一个分区产生一个进程，多个分区可能分散到不同机器上，如果对象在分区方法外进行实例化，则这个对象必须能被序列化，否则无法跨进程共享该对象）
      conn.setAutoCommit(false)
      val preparedStatement = conn.prepareStatement("INSERT INTO usertable(user_id,cell) VALUES (?,?);")
      iter.foreach(t => {  //遍历RDD分区中的元素 将每个元素插入mysql
        preparedStatement.setInt(1,t._1)
        preparedStatement.setString(2,t._2)
        preparedStatement.addBatch()
      })
      preparedStatement.executeBatch()
      conn.commit()  //遍历时先将数据添加到缓存中，全部遍历完成后commit提交数据 （如果单个分区数据量很大，则需要多次分批commit，可以记录一个count，达到一定数值再提交并清空临时变量，避免一次提交太多数据到mysql）
      conn.close()
    }
    )
    println("写入rdd数据到mysql成功")
  }



}

case class Person(name:String,age:Int)