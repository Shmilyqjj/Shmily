package base.classes

/**
 * :Description: 伴生类和伴生对象  Scala中无Static关键字，如果想实现Static效果可以采用伴生类
 * :Author: 佳境Shmily
 * :Create Time: 2020/6/25 11:16
 * :Site: shmily-qjj.top
 *
 * 在同一个scala文件中定义一个类，同时定义一个同名的object，那么它们就是伴生类和伴生对象的关系，可以互相直接访问私有的field。
 * 伴生对象通常会使用apply函数定义伴生类的构造方法。 这样在创建伴生类的对象时就可以省略 new 关键字。（Scala中的 apply 方法有着不同的含义,伴生类和伴生对象使用, 对于函数来说该方法意味着调用function本身）
 * 普通类只有在伴生对象中定义apply和unapply方法才能够利用伴生对象的方式创建对象和模式匹配
 *
 *
 * 关于apply和unapply
 * apply方法通常称为注入方法，在伴生对象中做一些初始化操作
 * apply方法的参数列表不需要和构造器的参数列表统一
 * unapply方法通常称为提取方法，使用unapply方法提取固定数量的参数来进行模式匹配
 * unapply方法会返回一个序列（Option），内部产生一个Some对象，Some对象存放一些值
 * apply方法和unapply方法会被隐式的调用
 *
 * apply接收一些参数生成对象，unapply提取一个对象里的值。
 */

class CompanionClass(val name:String, val id:Int){
  def hello = {println("Hello,my name is %s,my id is %s".format(name,id))}
  def hello1 = {println(s"Hello,my name is ${name},my id is ${id}")}
}

object CompanionClass {
  //object类名必须与class类完全一致
  private val version:Int = 1 //obj私有 version变量
  def apply(name: String, id: Int): CompanionClass = new CompanionClass(name, id)  //apply注入方法 new一个class对象
  def say = println("Hello")
  def hello:Unit = new CompanionClass("qjj",1).hello
  //伴生类 object里面的属性和方法就可以看作是带有static标识的。所以object中定义的方法 sayHi 不用使用类的实例调用，只能用静态的方式调用。

  def main(args: Array[String]): Unit = {
    CompanionClass.say  //不需要通过类对象而是直接通过类就可以访问  类似java 的static
    CompanionClass.hello
    println(CompanionClass.version)  //变量也相当于静态变量，可以直接通过类访问
  }
}
