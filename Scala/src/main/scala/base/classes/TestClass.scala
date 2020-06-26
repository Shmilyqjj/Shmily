package base.classes

/**
 * :Description:
 * :Author: 佳境Shmily
 * :Create Time: 2020/6/25 14:50
 * :Site: shmily-qjj.top
 *
 * Scala中的类不声明为public，一个Scala源文件中可以有多个类。
 */


class Point(xc:Int, yc:Int) {  //Scala 的类定义可以有参数，称为类参数，如这个xc, yc，类参数在整个类中都可以访问。
  var x: Int = xc
  var y: Int = yc
  def move(dx:Int, dy:Int): Unit ={
    x = x + dx
    y += dy
    println(s"点坐标 x: ${x},y: ${y}")
  }
}

//类的继承
class Location(val xc:Int,val yc:Int,val zc:Int) extends Point(xc,yc){
  var z = zc
  def move(dx:Int,dy:Int,dz:Int):Unit={
    x = x + dx
    y = y + dy
    z = z + dz
    println(s"x: $x,y:$y,z:$z")

  }
}

object TestClass{ //object与class区别是class有类参数而object没有类参数
  def main(args: Array[String]): Unit = {
    val tc = new Point(1,1)
    tc.move(1,1)

    val location = new Location(1,2,3)
    location.move(3,2,1)
  }
}


