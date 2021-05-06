package base.variables

/**
  * Description: Scala运算符
  * Date: 2020/5/24 10:06
  * Author: 佳境Shmily
  * Site: shmily-qjj.top
  */
object ScalaOperators {
  def main(args: Array[String]): Unit = {
    var a = 1
    var b = 2
    var c = 3
    var d = 4
    // 基础运算符  + - * / %

    //关系运算符 == !=  > <  >=  <=

    //逻辑运算符 && ||  !

    //位运算符 ~,&,|,^分别为取反，按位与与，按位与或，按位与异或运算  >>右移  <<左移  >>>无符号右移

    //赋值运算符 = += -= *= /= %= <<=  >>=  &=  ^=   |=
    c >>= d
    println(c)

    // 三目运算符
    val boolVal = if(a == 1) true else false
    println(boolVal)


  }
}
