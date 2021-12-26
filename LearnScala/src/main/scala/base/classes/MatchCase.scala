package base.classes

/**
 * :Description: Scala模式匹配
 * :Author: 佳境Shmily
 * :Create Time: 2020/6/26 10:56
 * :Site: shmily-qjj.top
 */
object MatchCase {

  def main(args: Array[String]): Unit = {
    println(op("+", 1, 2))
    println(op("-", 1, 2))
    println(op("*", 1, 2))
    println(op("/", 1, 2))
    println(op("r", 1, 2))
    println(op1("/", 1, 2))

    println("*************************")
    val p1 = new Person("zxw", 21)
    val p2 = new Person("qjj", 18)
    PersonOperation(p1)
    PersonOperation(p2)
    println("*************************")
    val l = List(p1, p2)
    PersonOperation1(l)
  }
  //Scala模式匹配
  //选择器 match {备选项}

  def op(x:String,a:Int,b:Int): Any ={
    var result:Any = 0
    x match {
      case "+" => result = a + b
      case "-" => result = a - b
      case "*" => result = a * b
      case "/" => result = a * 1.0 / b
      case _ => println("not a supported operation")  //这个case _ 相当于java中switch case里的default
    }
    result
  }

  def op1(x:String,a:Int,b:Int):Any = x match {
    case "+" => a + b
    case "-" => a - b
    case "*" => a * b
    case "/" => a * 1.0 / b
  }

  def PersonOperation(person:Person): Unit = person match {
    case Person("qjj", 22) => println("yes")
    case Person("zxw", 21) => println("yes")
    case _ => println("Not matched,who are you?")
  }

  def PersonOperation1(l:List[Person]): Unit = {
    l.foreach(_ match {
      case Person("qjj", 22) => println("yes")
      case Person("zxw", 21) => println("yes")
      case _ => println("Not matched,who are you?")
    })
  }



}

case class Person(name:String,age:Int)


