package com.study.spark.util.Tests

import java.io.IOException

import org.apache.hadoop.hbase.client.{ConnectionFactory, Delete, Put, Scan}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor, TableName}

/**
  * 从hbase中增删改查数据
  * Scala API 测试工具
  *
  */
object Hbase_ScalaAPI_Test {

  val configuration = HBaseConfiguration.create()
  val connection = ConnectionFactory.createConnection(configuration)
  val admin = connection.getAdmin
  val table = connection.getTable(TableName.valueOf("category_clickcount"))

  //创建一个hbase表
  def createTable(tableName: String, columnFamilys: Array[String]) = {
    //操作的表名
    val tName = TableName.valueOf(tableName)
    //当表不存在的时候创建Hbase表
    if (!admin.tableExists(tName)) {
      //创建Hbase表模式
      val descriptor = new HTableDescriptor(tName)
      //创建列簇i
      for (columnFamily <- columnFamilys) {
        descriptor.addFamily(new HColumnDescriptor(columnFamily))
      }
      //创建表
      admin.createTable(descriptor)
      println("create successful!!")
    }
  }

  //向hbase表中插入数据
  //put 'sk:test1','1','i:name','Luck2'
  def insertTable(rowkey: String, columnFamily: String, column: String, value: String) = {
    //准备key 的数据
    val puts = new Put(rowkey.getBytes())
    //添加列簇名,字段名,字段值value
    puts.addColumn(columnFamily.getBytes(), column.getBytes(), value.getBytes())
    //把数据插入到tbale中
    table.put(puts)
    println("insert successful!!")
  }

  //获取hbase表中的数据
  //scan 'sk:test1'
  def scanDataFromHTable(columnFamily: String, column: String) = {
    //定义scan对象
    val scan = new Scan()
    //添加列簇名称
    scan.addFamily(columnFamily.getBytes())
    //从table中抓取数据来scan
    val scanner = table.getScanner(scan)
    var result = scanner.next()
    //数据不为空时输出数据
    while (result != null) {
      println(s"rowkey:${Bytes.toString(result.getRow)},列簇:${columnFamily}:${column},value:${Bytes.toString(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(column)))}")
      result = scanner.next()
    }
    //通过scan取完数据后，记得要关闭ResultScanner，否则RegionServer可能会出现问题(对应的Server资源无法释放)
    scanner.close()
  }

  //删除某条记录
  //delete 'sk:test1','1','i:name'
  def deleteRecord(rowkey: String, columnFamily: String, column: String) = {
    val info = new Delete(Bytes.toBytes(rowkey))
    info.addColumn(columnFamily.getBytes(), column.getBytes())
    table.delete(info)
    println("delete successful!!")
  }

  // 关闭 connection 连接
  def close()={
    if (connection!=null){
      try{
        connection.close()
        println("关闭成功!")
      }catch{
        case e:IOException => println("关闭失败!")
      }
    }
  }

  def main(args: Array[String]): Unit = {
    //createTable("sk:test1",Array("i"))
    //insertTable("1", "i", "age", "22")
    //scanDataFromHTable("i", "age")
    deleteRecord("1","i","name")
    close()
  }
}
