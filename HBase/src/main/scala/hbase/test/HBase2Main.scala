package hbase.test

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{CellUtil, HBaseConfiguration, TableName}
import org.apache.hadoop.security.UserGroupInformation

import java.util

/**
 * HBase 2.x API
 *
 * HBaseConfiguration	用于连接hbase数据库，获取连接connection（DataBase)
 *
 * connection.get获取对应的操作对象
 * HBaseAdmin		创建表，删除表，列出表项，使表有效或无效，以及添加或删除表列簇成员等
 * HTable 				表内容操作对象：表内容的增删改查  HTable可以用来和 HBase 表直接通信  并非线程安全
 *
 * ColumnFamilyDescriptorBuilder		列簇修饰符 维护着关于列簇的信息，例如版本号，压缩设置等。它通常在创建表或者为表添加列 簇的时候使用。列簇被创建后不能直接修改，只能通过删除然后重新创建的方式
 *
 * TableDescriptorBuilder 		  表的列簇操作，增加删除列簇，设置表级别的参数，入表的压缩级别，存储时间等
 *
 * 四大对数据增删改查api，由HTable对象调用
 * HTable.
 * Put 用来对单个行执行添加操作
 * Get 用来获取单个行的相关信息
 * Delete 用来封装一个要删除的信息
 * Scan 用来封装一个作为查询条件的信息
 * 处理返回结果集
 * Result         存储 Get 或者 Scan 操作后获取表的单行值。使用此类提供的方法可以直接获取值或者各种 Map 结构（key-value 对）
 * ResultScanner  ResultScanner类把扫描操作转换为类似的get操作，它将每一行数据封装成一个Result实例，并将所有的Result实例放入一个迭代器中。
 */

object HBase2Main {

  def main(args: Array[String]): Unit = {
    //设置zookeeper的参数
    val conf: Configuration = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", "hbasezk1,hbasezk2,hbasezk3")
    conf.set("hbase.zookeeper.property.clientPort", "2181")
    System.setProperty("java.security.krb5.conf", "/opt/Env/Kerberos/krb5.conf")

    conf.set("hadoop.security.authentication", "Kerberos")
    conf.set("hbase.security.authentication", "Kerberos")
    conf.set("hbase.master.kerberos.principal", "hbase/_HOST@HIVETEST.COM")
    conf.set("hbase.regionserver.kerberos.principal", "hbase/_HOST@HIVETEST.COM")

    UserGroupInformation.setConfiguration(conf)
    UserGroupInformation.loginUserFromKeytab("hbase", "/opt/Env/Kerberos/hbase.keytab")
    HBaseAdmin.available(conf)


    //创建新表
    DeleteTable(conf, "qjj_test_api")
    CreateNewTable(conf, "qjj_test_api", "info", "grade")
    CreateNewTable(conf, "qjj_test_api1", "info", "grade")
    //删除表
    DeleteTable(conf, "qjj_test_api1")
    //添加数据
    InsertTable(conf, "qjj_test_api", "qjj1", "grade", "age", "23")
    InsertTable(conf, "qjj_test_api", "qjj1", "grade", "name", "qjj")
    InsertTable(conf, "qjj_test_api", "qjj1", "grade", "class", "38")
    //添加新的列簇
    InsertColumnFaimly(conf, "qjj_test_api", "cri")
    //查询所有的表信息
    ScanValue(conf, "qjj_test_api")
    //查找指定row的信息
    getRowKeyValue(conf, "qjj_test_api", "qjj1")
    //删除指定row的信息
    deleteRows(conf, "qjj_test_api", "qjj1")
  }

  /**
   * 在新API中,HTableDescriptor和HColumnDescriptor会逐渐被TableDescriptorBuilder和ColumnFamilyDescriptorBuilder取代
   * TableDescriptorBuilder 表描述生成器
   * ColumnFamilyDescriptorBuilder 列簇描述生成器
   *
   * @param conf
   * @param tablename
   * @param columnFamily
   */
  def CreateNewTable(conf: Configuration, tablename: String, columnFamily: String*): Unit = {
    //创建连接
    val conn: Connection = ConnectionFactory.createConnection(conf)
    //创建'库'操作 admin对象
    val admin = conn.getAdmin.asInstanceOf[HBaseAdmin]
    //将字符串转化为表名
    val tableName = TableName.valueOf(tablename)
    //TableDescriptorBuilder 表描述生成器
    val table: TableDescriptorBuilder = TableDescriptorBuilder.newBuilder(tableName)

    for (cf <- columnFamily) {
      //ColumnFamilyDescriptorBuilder 列簇描述生成器
      val info = ColumnFamilyDescriptorBuilder.of(cf)
      //添加列簇
      table.setColumnFamily(info)
    }

    //构建表描述
    val descriptor: TableDescriptor = table.build()
    //创建表
    admin.createTable(descriptor)
    //关闭接口
    admin.close()
    conn.close()
  }

  /**
   * 增加CF
   *
   * @param conf
   * @param tableName
   * @param ColumnFamily
   */
  def InsertColumnFaimly(conf: Configuration, tableName: String, ColumnFamily: String*): Unit = {
    //创建连接
    val conn = ConnectionFactory.createConnection(conf)

    //创建'库'操作 admin对象
    val admin: HBaseAdmin = conn.getAdmin.asInstanceOf[HBaseAdmin]

    //遍历插入要插入的列簇##(表)
    for (cf <- ColumnFamily) {

      //通过.of方法,转化列簇名
      val cff = ColumnFamilyDescriptorBuilder.of(cf)

      admin.addColumnFamily(TableName.valueOf(tableName), cff)
    }

    admin.close()
    conn.close()
  }

  /**
   * 删表
   *
   * @param conf
   * @param tablename
   */
  def DeleteTable(conf: Configuration, tablename: String): Unit = {
    //创建连接
    val conn: Connection = ConnectionFactory.createConnection(conf)
    //创建'库'操作 admin对象
    val admin = conn.getAdmin.asInstanceOf[HBaseAdmin]
    //将String类型字符串,修饰为表名
    val tableName = TableName.valueOf(tablename)
    //使表下架
    admin.disableTable(tableName)
    //删除表
    admin.deleteTable(tableName)
    //关闭接口
    admin.close()
    conn.close()
  }

  /**
   * 插入数据
   *
   * @param conf
   * @param tableName
   * @param rowkey
   * @param columnFamily
   * @param column
   * @param value
   */
  def InsertTable(conf: Configuration, tableName: String, rowkey: String, columnFamily: String, column: String, value: String): Unit = {
    //创建连接
    val conn: Connection = ConnectionFactory.createConnection(conf)

    val name = TableName.valueOf(tableName)
    //创建表连接对象
    val table: Table = conn.getTable(name)
    //创建put对象,传递rowKey
    val put: Put = new Put(Bytes.toBytes(rowkey))
    //添加属性
    put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value))
    //添加数据到表
    table.put(put)
    table.close()
    conn.close()
  }

  /**
   * 查询表信息
   *
   * @param conf
   * @param tableName
   */
  def ScanValue(conf: Configuration, tableName: String): Unit = {

    val conn = ConnectionFactory.createConnection(conf)

    val tablename = TableName.valueOf(tableName)
    //获取表
    val table = conn.getTable(tablename)
    //创建扫描 对象
    val scan = new Scan()
    //获取扫描结果
    val scanner: ResultScanner = table.getScanner(scan)

    //取该行的cell迭代器
    /* val scanner: CellScanner = result.cellScanner()
    // 迭代这一行的cell
       while(scanner.advance()){
         val cell = scanner.current()
         println(s"RowKey:${Bytes.toString(CellUtil.cloneRow(cell))}")
         println(s"ColumFamily:${Bytes.toString(CellUtil.cloneFamily(cell))}")
         println(s"Qualifier:${Bytes.toString(CellUtil.cloneQualifier(cell))}")
         println(s"Value:${Bytes.toString(CellUtil.cloneValue(cell))}")
         println("==================================================================")
       }
     */

    val cells = scanner.next().rawCells()
    for (cell <- cells) {
      println(s"RowKey:${Bytes.toString(CellUtil.cloneRow(cell))}")
      println(s"ColumFamily:${Bytes.toString(CellUtil.cloneFamily(cell))}")
      println(s"Qualifier:${Bytes.toString(CellUtil.cloneQualifier(cell))}")
      println(s"Value:${Bytes.toString(CellUtil.cloneValue(cell))}")
      println("==================================================================")
    }

    table.close()
    conn.close()
  }

  /**
   * 根据rowkey获取值
   *
   * @param conf
   * @param tableName
   * @param RowKey
   */
  def getRowKeyValue(conf: Configuration, tableName: String, RowKey: String): Unit = {

    val conn = ConnectionFactory.createConnection(conf)

    val tablename: TableName = TableName.valueOf(tableName)

    val table: Table = conn.getTable(tablename)
    //通过get对象查询数据
    val get: Get = new Get(Bytes.toBytes(RowKey))

    val result: Result = table.get(get)

    //查找指定表,指定属性的值
    println(s"Value:${Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("name")))}")

    val cells = result.rawCells()

    for (cell <- cells) {
      println(s"Row:${result.getRow}")
      println(s"Row:${Bytes.toString(CellUtil.cloneRow(cell))}")
      println(s"ColumFamily:${Bytes.toString(CellUtil.cloneFamily(cell))}")
      println(s"Qualifier:${Bytes.toString(CellUtil.cloneQualifier(cell))}")
      println(s"Value:${Bytes.toString(CellUtil.cloneValue(cell))}")
      println(s"TimeStamp:${cell.getTimestamp}")
      println("=======================================")
    }

    table.close()
    conn.close()
  }

  /**
   * 删除指定row
   *
   * @param conf
   * @param tableName
   * @param RowKey
   */
  def deleteRows(conf: Configuration, tableName: String, RowKey: String*): Unit = {
    val conn: Connection = ConnectionFactory.createConnection(conf)
    val table: Table = conn.getTable(TableName.valueOf(tableName))

    val list: util.ArrayList[Delete] = new util.ArrayList[Delete]()
    for (row <- RowKey) {
      val delete: Delete = new Delete(Bytes.toBytes(row))
      list.add(delete)
    }
    table.delete(list)
    table.close()
    conn.close()
  }


}
