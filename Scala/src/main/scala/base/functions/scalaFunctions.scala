package base.functions

/**
 * Scala方法，函数
 *
 * def functionName ([参数列表]) : [return type] = {
 * function body
 * return [expr]
 * }
 */

object scalaFunctions {
  def main(args: Array[String]): Unit = {
    println(addValue(1,2,3))
    println(addValue(1,2,3,4,5))

    // scala闭包 闭包是一个函数，返回值依赖于声明在函数外部的一个或多个变量。
    val x = 2
    val closure = (i:Int) => i * x
    println(closure(1))
    println(closure(2))

    println("**********************")
    println(op("+", 1, 2))
    println(op("-", 1, 2))
    println(op("*", 1, 2))
    println(op("/", 1, 2))
    println(op1("/", 1, 2))
  }

  def addValue(args: Int*): Int = {  //普通函数  多参数函数
    var i = 0
    for(arg <- args){
      i += arg
    }
    i
  }

  def op(x:String,a:Int,b:Int): Any ={
    var result:Any = 0
    x match {
      case "+" => result = a + b
      case "-" => result = a - b
      case "*" => result = a * b
      case "/" => result = a * 1.0 / b
    }
    result
  }

  def op1(x:String,a:Int,b:Int):Any = x match {
    case "+" => a + b
    case "-" => a - b
    case "*" => a * b
    case "/" => a * 1.0 / b
  }

}
