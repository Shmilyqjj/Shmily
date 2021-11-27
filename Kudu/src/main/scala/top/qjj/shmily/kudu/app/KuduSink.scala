package top.qjj.shmily.kudu.app
import org.apache.spark.sql.{ForeachWriter, Row}
import scala.util.parsing.json.JSON

/**
 * :Description:Kudu sink 操作kudu数据落地  在每个batch中，这三个方法各调用一次，相当每批数据调用一次
 * :Author: 佳境Shmily
 * :Create Time: 2021/11/27 11:56
 * :Site: shmily-qjj.top
 */
class KuduSink(kuduMasterAddrs:String) extends ForeachWriter[Row](){
  override def open(partitionId: Long, epochId: Long): Boolean = {
    println(s"KuduSink partitionId:$partitionId epochId:$epochId kuduMasterAddrs:$kuduMasterAddrs")
    // 连接Kudu
    true
  }

  override def process(value: Row): Unit = {
    //解析json 拆分请求类型  进行不同类型的Kudu操作
    val jsonStr:String = value.toString().stripPrefix("[").stripSuffix("]")
    val msg:Map[String,Any] = regJson(JSON.parseFull(jsonStr))
    //必备参数
    val option = msg.get("op_type")
    val kuduTable = msg.get("kudu_table")
    //可能出现的参数
    val cols = msg.get("cols")
    val pk = msg.get("pk")
    val partition_type = msg.get("partition_type")
    val column_name = msg.get("column_name")
    val data = msg.get("data")
//    println(s"$option -- $kuduTable -- $cols -- $pk -- $partition_type -- $column_name -- $data")

    //参数判断
    def checkParam(para:Option[Any]): String ={
      try{
        para.get.toString
      }catch {
        case ex: java.util.NoSuchElementException => throw new RuntimeException(s"NoSuchElementException 缺少必要参数");
        case ex: Exception => throw new RuntimeException(s"Error: ${ex.toString}")
      }
    }

    checkParam(option) match {
      //DDL
      case "create_table" => {
        //调用建表API
        println("create_table")
      }
      case "delete_table" => {
        //调用删表API
        println("delete_table")
      }
      case "delete_column" => {
        //调用删字段API
        println("delete_column")
      }
      //DML
      case "delete_data" => {
        //调用删数据API
        println("delete_data")
      }
      case "insert_data" => {
        //调用插入数据API
        println("insert_data")
      }
      case "update_data" => {
        //调用更新数据API
        println("update_data")
      }
    }



  }

  override def close(errorOrNull: Throwable): Unit = {
    // 关闭连接
  }

  /**
   * 解析单层json对象  用法regJson(JSON.parseFull(str))
   * @param json
   * @return
   */
  def regJson(json:Option[Any]):Map[String,Any] = json match {
    case Some(map:Map[String,Any]) => map
  }

}


