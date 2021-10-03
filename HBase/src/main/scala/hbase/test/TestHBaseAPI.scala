package hbase.test

import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{ConnectionFactory, HTable}


object TestHBaseAPI {
  def main(args: Array[String]): Unit = {
    val oldZks = ""
    val newZks = ""
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.property.clientPort", "2181")
    conf.set("hbase.zookeeper.quorum", newZks)
    conf.set("hbase.client.keyvalue.maxsize", "209715200")
    val conn = ConnectionFactory.createConnection(conf)

    println(conn.getAdmin.tableExists(TableName.valueOf("test_qjj")))
  }
}
