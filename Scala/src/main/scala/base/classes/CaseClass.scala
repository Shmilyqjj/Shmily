package base.classes

/**
 * :Description: Scala样例类
 * :Author: 佳境Shmily
 * :Create Time: 2020/6/26 11:08
 * :Site: shmily-qjj.top
 *
 * 使用了case关键字的类定义就是就是样例类(case classes)，样例类是种特殊的类
 * case class在模式匹配和Actor中常用
 * 样例类默认帮你实现了toString,equals，copy和hashCode等方法
 * 样例类可以new, 也可以不用new
 *
 * 创建样例类后：
 * 构造器的每个参数都成为val，除非显式被声明为var，但是并不推荐这么做；
 * 在伴生对象中提供了apply方法，所以可以不使用new关键字就可构建对象；
 * 提供unapply方法使模式匹配可以工作；
 * 生成toString、equals、hashCode和copy方法，除非显示给出这些方法的定义。
 */
case class CaseClass(name:String, id:Int)  //可以封装数据，可用于模式匹配

case object CaseObject  //不可封装数据，可用于模式匹配
