package base.functions
import scala.util.matching.Regex
/**
 * :Description: Scala正则表达式
 * :Author: 佳境Shmily
 * :Create Time: 2020/6/26 11:34
 * :Site: shmily-qjj.top
 *
 * Scala 通过 scala.util.matching 包中的 Regex 类来支持正则表达式
 */
object ScalaRegex {
  def main(args: Array[String]): Unit = {
    val pattern = "Scala".r  //String的r方法构造一个Regex对象
    val str: String = "Scala language is easy!"
    (pattern findAllMatchIn(str)).foreach(println(_))  // findAllMatchIn找到所有匹配项 得到一个Iterator 遍历Iterator得到匹配到的数据

    println("*****************************************")

    val pattern1 = new Regex("(S|s)cala")  //生成Regex对象
    val str1: String = "Scala == scala!"
    pattern1.findAllMatchIn(str1).foreach(println)
  }
}
