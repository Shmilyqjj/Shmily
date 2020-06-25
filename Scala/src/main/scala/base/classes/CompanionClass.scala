package base.classes

/**
 * :Description: 伴生类和伴生对象  Scala中无Static关键字，如果想实现Static效果可以采用伴生类
 * :Author: 佳境Shmily
 * :Create Time: 2020/6/25 11:16
 * :Site: shmily-qjj.top
 *
 * 在同一个scala文件中定义一个类，同时定义一个同名的object，那么它们就是伴生类和伴生对象的关系，可以互相直接访问私有的field。
 * 伴生对象通常会使用apply函数定义伴生类的构造方法。 这样在创建伴生类的对象时就可以省略 new 关键字。（Scala中的 apply 方法有着不同的含义,伴生类和伴生对象使用, 对于函数来说该方法意味着调用function本身）
 */

class CompanionClass(val name:String, val id:Int){
  def hello = {println("Hello,my name is %s,my id is %s".format(name,id))}
  def hello1 = {println(s"Hello,my name is ${name},my id is ${id}")}
}

object CompanionClass {
  //object类名必须与class类完全一致
  private val version:Int = 1 //obj私有 version变量
  def apply(name: String, id: Int): CompanionClass = new CompanionClass(name, id)
  def say = println("Hello")
  def hello:Unit = new CompanionClass("qjj",1).hello
  //伴生类 object里面的属性和方法就可以看作是带有static标识的。所以object中定义的方法 sayHi 不用使用类的实例调用，只能用静态的方式调用。

  def main(args: Array[String]): Unit = {
    CompanionClass.say  //不需要通过类对象而是直接通过类就可以访问  类似java 的static
    CompanionClass.hello
    println(CompanionClass.version)  //变量也相当于静态变量，可以直接通过类访问
  }
}
