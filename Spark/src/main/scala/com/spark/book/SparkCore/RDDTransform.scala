package com.spark.book.SparkCore

import org.apache.spark.{SparkConf, SparkContext}

/**
 * :Description: RDD转换函数  转换操作
 * :Author: 佳境Shmily
 * :Create Time: 2020/6/27 11:30
 * :Site: shmily-qjj.top
 *
 * RDD是不可变的数据集，不可以修改RDD数据，但RDD中数据可以通过转换函数转换形态，生成含有新的数据的新的RDD
 */
object RDDTransform {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("MyWordCount")
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(1 to 10)  //rdd 1-10
    val rdd1 = sc.parallelize(Array("1,2,3","4,5,6","7,8,9"))
    val rdd2 = sc.parallelize(Array(1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9))
    val rdd3 = sc.parallelize(Array(1,3,5,7,9,11,13,15,17,19,21), 3)


    //map  对每个元素应用一个方法
    rdd.map(_ * 10).map(x => (x,1)).collect().foreach(print)
    println()

    //mapPartitions 与map功能相似，但map是一个一个元素处理，mapPartitions是按RDD分区来处理，先遍历分区再遍历分区中的元素
    val stuRDD = sc.parallelize(Array(("01",83),("02",97),("03",100),("04",95),("05",87)), 2)  //创建两个分区的数据
    stuRDD.mapPartitions(iter => {
      var result = List[String]()  //必须指定List元素类型
      while (iter.hasNext){
        result = iter.next() match {
          case (id, grade) if grade >= 95 => (id + "_" + grade) :: result  //拼接高于95分的学生的学号和成绩 并追加到list
          case _ => result
        }
      }
      result.iterator  //函数必须返回iterator类型
    }).collect().foreach(println)

    //mapPartitionsWithIndex  与mapPartitions类似，但f会传入当前分区号
    stuRDD.mapPartitionsWithIndex((index, iter) => {
     var result = List[String]()
     while (iter.hasNext){
       result = iter.next() match {
         case (id, grade) if grade >= 95 => id + "_" + grade + "_" + index :: result
         case _ => result
       }
     }
      result.iterator
    }).collect().foreach(println)

    //flatMap  与map相似，但会拆出RDD中每个集合元素中包含的多个元素
    rdd1.flatMap(_.split(",")).collect().foreach(x => print(x + " "))
    println()

    //filter 过滤 判断 x => f true false
    rdd.filter(x => x%2 != 0).collect().foreach(x => print(x + " "))

    //distinct
    rdd2.distinct().collect()
    rdd2.distinct(2).collect()  //去重可以指定新RDD的分区个数

    //union 两个RDD求并集
    rdd.union(rdd2).collect().foreach(x => print(x + " "))
    println()

    //intersection 两个RDD求交集
    val rddAndRdd1 = rdd2.intersection(rdd)
    rddAndRdd1.collect.foreach(x => print(x + " "))
    println()

    //subtract  两个RDD求差集
    val rddSub = rdd.subtract(rdd2)  //返回rdd中有但rdd2中没有的元素
    rddSub.collect().foreach(println)

    //coalesce 改变分区数 分区较多的RDD转换为分区较少的RDD不需要Shuffle，少->多则必须要Shuffle  第二个参数shuffle默认false
    println(rdd3.coalesce(1).getNumPartitions)  //rdd有三个分区，coalesce1后变为一个分区
    println(rdd3.coalesce(6).partitions.length) //rdd有三个分区，coalesce6后仍为3个分区，不能扩展更多，操作不生效
    println(rdd3.coalesce(6,true).partitions.length) //rdd有三个分区，coalesce6且指定shuffle为true后扩展为6个分区
    //repartition 扩展分区数 底层调用coalesce且shuffle参数默认为true，无论多转少还是少转多都可以生效  等价于coalesce(x,true)
    println(rdd3.repartition(1).getNumPartitions)

    //randomSplit操作 根据weights权重来拆分RDD为多个RDD weights设置为Array(1,2,3)则产生3个RDD元素个数比大致为2:3:5 （权重比例之和最好为1，否则可能打不到预期效果）
    val splittedRDDArray = rdd2.randomSplit(Array(2,3,5))
    splittedRDDArray.foreach(x => println(x.count()))

    //glom操作 将RDD中每个分区数据变成一个数组Array
    val arrs = rdd3.glom().collect()
    arrs.foreach(_.foreach(x => print(x + " ")))

    //zip 两个RDD元素以k-v形式合并 key为rdd1中的元素 value为rdd2中元素
    val zipRDD1 = sc.parallelize(1 to 5, 2)
    val zipRDD2 = sc.parallelize(Array("a","b","c","d","e"), 2)
//    val zipRDD2 = sc.parallelize(Array("a","b","c","d","e"), 1)  //分区数不一致会报错
    val zippedRDD = zipRDD1.zip(zipRDD2)
    zippedRDD.collect().foreach(println)


  }


}
