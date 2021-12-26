package base.classes

/**
 * :Description: Scala异常处理
 * :Author: 佳境Shmily
 * :Create Time: 2020/6/26 11:46
 * :Site: shmily-qjj.top
 */
object ScalaExceprion{
  def main(args: Array[String]): Unit = {
    //通过throw new xxException抛出异常
    //if(1 == 1) throw new Exception("1 == 1 is True") else println("OK")

    println(method(1,2))
    println(method(11,0))
  }


  def method(a:Int,b:Int): Int ={
    /**
     * 异常捕捉的机制与其他语言中一样，如果有异常发生，catch字句是按次序捕捉的。因此，在catch字句中，越具体的异常越要靠前，越普遍的异常越靠后。
     * 如果抛出的异常不在catch字句中，该异常则无法处理，会被升级到调用者处。
     * 捕捉异常的catch子句，语法与其他语言中不太一样。
     * 在Scala里，借用了模式匹配的思想来做异常的匹配，因此，在catch的代码里，是一系列case字句
     */
    var result = 0
    try{
      println("try first but not return")
      result = a / b
      result
    }catch{
      case ex:IndexOutOfBoundsException => println("IndexOutOfBoundsException");result
      case ex:ArithmeticException => println("ArithmeticException");result
      case ex:Exception => println("Other error") ;result
      case _ => println("????");result
    }finally {
      println("Finally Second and maybe return")
    }
  }
}
