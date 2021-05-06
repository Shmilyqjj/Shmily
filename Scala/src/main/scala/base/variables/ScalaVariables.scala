package base.variables


/**
  * Description: Scala语言 变量
  * Date: 2020/5/23 16:35
  * Author: 佳境Shmily
  * Site: shmily-qjj.top
  */
object ScalaVariables {
  def main(args: Array[String]): Unit = {  //所有的方法名称的第一个字母用小写，如果若干单词被用于构成方法的名称，则每个单词的第一个字母应大写（mainMethod）
    // Scala 纯面向对象语言 每个值都是对象
    // Scala提供了轻量级的语法用以定义匿名函数，支持高阶函数，允许嵌套多层函数，并支持柯里化(多层函数首个函数只有一个参数)。
    // Scala的case class及其内置的模式匹配相当于函数式编程语言中常用的代数类型。

    // Scala是面向行的语言，分割可以用;或者换行符
    val a = "qjj";print(a)
    //import语句可以出现在任何地方，而不是只能在文件顶部。import的效果从开始延伸到语句块的结束。这可以大幅减少名称冲突的可能性。
    import java.lang.System.out.print
    print("\njava print")
    import java.lang.System.out.{println => javaPrintln}  //引用包并赋予别名
    javaPrintln("\njava print")
    import java.util.{HashMap => _, _}  // 引入了util包的所有成员，但是HashMap被隐藏了





    // Scala数据类型
    /**
      * Byte	8位有符号补码整数。数值区间为 -128 到 127
      * Short	16位有符号补码整数。数值区间为 -32768 到 32767
      * Int	32位有符号补码整数。数值区间为 -2147483648 到 2147483647
      * Long	64位有符号补码整数。数值区间为 -9223372036854775808 到 9223372036854775807
      * Float	32 位, IEEE 754 标准的单精度浮点数
      * Double	64 位 IEEE 754 标准的双精度浮点数
      * Char	16位无符号Unicode字符, 区间值为 U+0000 到 U+FFFF
      * String	字符序列
      * Boolean	true或false
      * Unit	表示无值，和其他语言中void等同。用作不返回任何结果的方法的结果类型。Unit只有一个实例值，写成()。
      * Null	null 或空引用
      * Nothing	Nothing类型在Scala的类层级的最底端；它是任何其他类型的子类型。
      * Any	Any是所有其他类的超类
      * AnyRef	AnyRef类是Scala里所有引用类(reference class)的基类
      */
    // Scala字面量 举例：
    //Int
    val intVal = 0
    val intVal1 = 0xFFFFFFFF;print(intVal1)
    //Long
    val longVal = 1L
    //Float
    val floatVal = 0.0
    val floatVal1 = 3.1415f
    val floatVal2 = 1.0e10
    val floatVal3 = .1
    //Boolean
    var boolVal = false
    boolVal = true
    //Char
    var charVal = 'a'
    charVal = '\u0041'
    charVal = '\t'
    //String
    var stringVal : String = "qjj"
    stringVal =
      """
        |hello
        |qjj
        |zxw
        |study
        |Scala
        |""".stripMargin;print(stringVal)
    stringVal ="""
qjj1
qjj2
""";print(stringVal)


    // Scala使用var声明变量 使用val声明常量
    // Scala变量或常量声明时不一定需要指定数据类型，如果未指定则根据初始值推断出类型，所以数据类型和初始值必须要有一个存在，否则报错。
    val str : String = null
    val str1 = "qjj"
    val str2 : String = "qjj"
    // Scala支持同时声明多个变量为同一值
    val m, n = 100
    // 声明元组
    val tup = (1, "qjj")
    val tup1 : (Int, String) = (2, "Zxw")
    println(tup._1)
    println(tup1._2)


    //类型转换  使用toXX
    var num = 1;println("print" + num)  // num自动转为字符串
    val char1: Char = 'a';println("a ascii: " + char1.toInt)  // char转int（ascii）
    val char2 : Char = '2';println(char2.toDouble)
    //Byte和Short运算时都转为Int类型运算

    //标识符命名规范：首字符为字母，后续字符任意字母和数字，美元符号，可后接下划线_  数字不可以开头。   首字符为操作符(比如+ - * / )，后续字符也需跟操作符 ,至少一个  用反引号`....`包括的任意字符串，即使是关键字(39个)也可以 [比如true]
    val ++ = 0;var +-*/ = 1; var $52ab_ = 0;var `true` = 1 //scala没有++ --运算符













  }
}
