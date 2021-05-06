package base.circulation

object forEach {
  def main(args: Array[String]): Unit = {
    val l = Array(1,2,3,"qjj","zxw")
    val n = Array(1,2,3,4,5)
    n.foreach((i:Int) => print(i))
    println()
    l.foreach(print(_))
    println()
    l.foreach(i => print(i))
    println()
    l.foreach(print)
    println()
  }
}
