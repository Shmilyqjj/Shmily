package base.functions

import java.io.{File, PrintWriter}
import scala.io.Source

/**
 * :Description:Scala中的IO
 * :Author: 佳境Shmily
 * :Create Time: 2020/6/26 13:26
 * :Site: shmily-qjj.top
 *
 * Scala文件操作都直接使用Java IO类 直接用（java.io.File)
 */
object ScalaIO {
  def main(args: Array[String]): Unit = {
//    writeData()
//    ReadFile()
  ReadInputLine()
  }

  def writeData(): Unit ={
    //写文件
    val filePath = "D:\\Data\\TestData\\a.txt"
    val writer = new PrintWriter(new File(filePath))
    writer.write("haha\n")
    writer.close()
  }

  def ReadFile():Unit = {
    //读取文件内容 使用scala.io.Source
    val filePath = "D:\\Data\\TestData\\a.txt"
    Source.fromFile(filePath).foreach(x => print(x + " "))  //单个字母读
  }

  def ReadInputLine():Unit = {
    print("请输入:")
    import scala.io.StdIn.readLine
    val s = readLine()
    println(s)
  }
}
