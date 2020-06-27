package com.study.spark.util

import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

/**
 * 对CategoryClickCount的Hbase表进行操作  操作过程调用HBaseUtils JavaAPI
 */

object CategoryClickCountDAO {

  val tableName = "category_clickcount"
  val cf = "info"  //column family
  val qulifier = "click_count"  //保存的click_count

  //save方法 保存数据到Hbase(调用HbaseUtil
  def save(list:ListBuffer[CategoryClickCount]) = {
    val table = HbaseUtils.getInstance().getTable(tableName)  //得到Hbase的表
    for (x <- list){
      //向table插入数据  incrementColumnValue值会自动累加
      table.incrementColumnValue(Bytes.toBytes(x.categoryId),Bytes.toBytes(cf),Bytes.toBytes(qulifier),x.clickCount)
    }
    println("Save CategoryClickCount Successfully")
  }


  //获取数据方法
  def count(day_category:String) :Long = {
    val table = HbaseUtils.getInstance().getTable(tableName)
    val get = new Get(Bytes.toBytes(day_category)) //通过rowkey获取  rowkey设计是day_category

    val value = table.get(get).getValue(Bytes.toBytes(cf),Bytes.toBytes(qulifier))
    //或者下面方式也一样
    //    get.addColumn(Bytes.toBytes(cf),Bytes.toBytes(qulifier))
    //    val value = table.get(get)

    if(value == null){
      return 0L //直接返回Long类型值
    }else{
      Bytes.toLong(value)
    }
  }




  //Test method
  def main(args: Array[String]): Unit = {

    //save方法测试
    //    val list = new ListBuffer[CategoryClickCount]
    //    list.append(CategoryClickCount("20190612_4",300))
    //    list.append(CategoryClickCount("20190612_5",600))
    //    list.append(CategoryClickCount("20190612_3",1128))
    //    save(list)
    //    println(count("20190612_4")+"-----"+count("20190612_5")+"-----"+count("20190612_3"))

  }

}
