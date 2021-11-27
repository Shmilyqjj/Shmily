/**
 * :Description: jsonstring to map
 * :Author: 佳境Shmily
 * :Create Time: 2021/11/27 13:19
 * :Site: shmily-qjj.top
 */
import scala.util.parsing.json.JSON

object TestJson {
  def main(args: Array[String]): Unit = {

    val jsTest = "{\"a\":1, \"b\":\"2\"}"
    println(regJson(JSON.parseFull(jsTest)))
  }

  def regJson(json:Option[Any]):Map[String,Any] = json match {
    case Some(map:Map[String,Any]) => map
  }
}