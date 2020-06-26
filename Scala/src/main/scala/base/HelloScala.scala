package base


import scala.util.control.Breaks._ //._ 相当于java的.*
object Hello {   //创建一个Hello对象
  def main(args: Array[String]): Unit = { //对象里的方法  先写参数名称:后写参数类型Array(String) unit空类型
    print("hello\tscala\nhello\\world\n-----------------------\n") //分号可以不加

    //scala的输出方式-三种
    val name = "qjj"
    val age = 20
    val url = "www.baidu.com"
    val sal = 200.1234d
    println("name="+name+" age="+age+" url="+url) //类似java
    printf("name=%s age=%d url=%s sal=%f sal0=%.4f\n",name,age,url,sal,sal)  //类似C  输出double类型用%f默认六位 %.4f控制小数点后四位
    println(s"name=$name,age=$age.url=$url sal=$sal")
    println(s"name=${name+"zxw"},age=${age+2}.url=$url\n-----------------------")      //类似php 必须s开头 可以用大括号直接对输出的元素进行操作

    //变量
    var xx:Int = 0 //var或者val 注意：var可以被多次赋值 val只能赋值一次  变量名:变量类型（可省略） = 初始化的值
    val xxx:Double = 0.52364
    var xxxx = 1
        xxxx = 2     //变量类型反推之后该变量类型确定，无法赋值为其他类型(与python不同)
//    xxxx = "wdwd"  //错误

    var num1:Int = 10
    println("num1"+num1) //此时自动将Int转为字符串
    val char1:Char ='a'
    println("char1 ="+char1.toInt)//toInt，字符转数字 toDounle toByte.... 注意只能低精度转高精度,高转低要强转（精度丢失）
    println("数字转字符串方法"+num1+"") //加空字符串直接数字转字符串
    val num:Int = 2.7.toInt;println(num) //强转
    val num2:Int = 10*3.5.toInt+6*1.5.toInt;var num3:Int =(10*3.5+6*1.5).toInt;println(num2+" "+num3) //先执行toInt方法，再执行乘法
    //Byte和Short运算时都转为Int类型运算
    //标识符命名规范：首字符为字母，后续字符任意字母和数字，美元符号，可后接下划线_  数字不可以开头。   首字符为操作符(比如+ - * / )，后续字符也需跟操作符 ,至少一个  用反引号`....`包括的任意字符串，即使是关键字(39个)也可以 [比如true]
    val ++ = 0;var +-*/ = 1; var $52ab_ = 0;var `true` = 1 //scala没有++ --运算符

    //if-else与java的相同
    var yy = 3
    var yyy = if(yy>5) true else false //代替三目运算符
    println(yyy);println("-----------------------")

    //循环
    for(i<- 1 to 4){ // 1 to 4分别赋值 -> 给i 包含4
      print(i + " ")
    }
    println()
    for(i<- 1 until 5){ //1 until 5 把1直到5（不包含5）分别赋值给i
      print(i+" ")
    }
    println()
    //循环守卫 - 类似于continue
    for(i<- 1 to 4 if i != 2){
      print(i+" ")
    }
    println()
    for(i<- 1 to 4 ; j = 4 - i){
      print(j+" ")
    }
    println()
    //嵌套循环
    for(i <- 1 to 4; j <- 1 to 4){
      print("i="+i+"j="+j+" ")
    }
    println()
    //yield
    var res = for(i <- 1 to 10) yield i
    print(res) //会输出一个Vector集合
    println()
    var res0 = for(i<- 1 to 10) yield{
      i+1
    }
    print(res0)
    println()
    for(i <- Range(1,10,2)){ //1到10每2次输出一次
      print(i+" ")
    }
    //while循环和do...while循环与java语法相同
    println()
    //scala删除了break和continue，continue用循环守卫，函数式的风格解决break
    var n=0
    breakable( //防止报异常
      while(n<20){
        print(n+" ")
        if(n == 10){
          break()
        }
        n += 1
      }
    )
    println()
    println("-----------------------")

    //函数
    def test(n:Int,m:Int){
      println("n="+n+" m="+m+" ")
    }
    test(1,2)
    def test0(n:Int): Unit ={ }//无返回值 void
    def test1(n:Int):Int = { //Int返回值
     return n+1 //return 可以省略
    }
    println(test1(5))
    def test2(n:Int=2):Int={//参数默认值
      n
    }
    println(test2()+" "+test2(4))
    println("-----------------------")

    //惰性函数 - 需要的时候才调用
    lazy val res1 = sum(3,5) //声明变量的时候不调用，真正使用这个lazy修饰的变量的时候才调用  lazy不能修饰var
    def sum(m:Int,n:Int):Int={
      println("sum()执行")
      return m + n
    }
    println("waiting...")
    println(res1)
    println("-----------------------")

    //val和var val和var一旦被定义，它的数据类型就不可以改变
    val abc = 0
    var cde = 0
//    abc = 1
    cde = 1 //var可以被多次赋值 val只能被赋值一次
//    cde ="ssd"

    //scala异常
    try{
      val i = 0;val b = 10;
      val c = b/i
      println(c)
    }catch{
      case ex:ArithmeticException => println("捕获除数为0的异常")
      case ex:Exception => println("捕获异常")  //也可以 throw new exception

    }finally {
      println("Finally 执行")
    }

    println("-----------------------")

    //类以及调用
    var cat = new Cat //括号可加可不加
    println(cat.name)

    var dog = new Dog("zxw",5,4)

  }
}

class Cat{
  //方法里的变量默认是私有的，但是编译过程中会默认生成get set方法，调用过程中也是调用get set方法
  var name = "huahua"
  var age = 2
  var ** :Int = _  //下划线是一个默认值 0
  def say(): Unit ={
    print("miaomiao")
  }
}

//构造器-构造方法
class Dog{
    //构造方法重载   （注意重载和重写的区别跟java一样：重写是外壳不变内容重写 重载是方法名相同参数不同-参数个数参数类型不同）
  var ++ = "a";
  var ** = 0
  def this(a:String,b:Int,c:Int){
      this() //固定写法，调用主构造函数
      println("构造函数"+a+" "+b+" "+c)
    }
}