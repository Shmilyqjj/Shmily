/**
 * :Description:
 * :Author: 佳境Shmily
 * :Create Time: 2021/11/27 13:49
 * :Site: shmily-qjj.top
 */
object TestMapVal {
  def main(args: Array[String]): Unit = {
    //Set差集
    val msg:Map[String,String] = Map("1"->"qjj","2"->"abc","3"->"hah","4"->"hah")
    println(msg.keySet)
    val s = msg.keySet-("2","1")
    println(s.toList.mkString(","))

    //Some() None()
    val s1 = Some("aaaaa")
    println(s1.get)
    val s2 = Some()
    println(s2.get)
    try{
      val s3 = None
      println(s3.get)
    }catch {
      case ex: java.util.NoSuchElementException => println("NoSuchElementException 参数为空")
      case ex: Exception => println("其他错误")
    }
    def checkParam(para:Option[String]): String ={
      para match {
        case Some(p) => p
        case None => throw new RuntimeException("")
      }
    }
    checkParam(Some("222"))
    checkParam(None)
    println("--------------------------------")

  }
}
