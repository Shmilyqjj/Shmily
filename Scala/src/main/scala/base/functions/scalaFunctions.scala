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

  }

  def addValue(args: Int*): Int = {  //普通函数  多参数函数
    var i = 0
    for(arg <- args){
      i += arg
    }
    i
  }


}
