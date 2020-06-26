package base.classes

/**
 * :Description:Scala特征
 * :Author: 佳境Shmily
 * :Create Time: 2020/6/26 9:45
 * :Site: shmily-qjj.top
 *
 * Scala Trait相当于java的接口当时它可以定义属性和方法
 * 可以使用Trait（特征）来继承多个父类实现多继承
 * Trait定义方式与类相似，只是使用Trait关键字而已
 * 子类继承特征可以实现未被实现的方法
 */
trait ScalaTrait{
  def A(x:Any): Boolean
  def B(x:Any): Boolean
}

class C() extends ScalaTrait{
  //特征也可以有构造器，由字段的初始化和其他特征体中的语句构成。这些语句在任何混入该特征的对象在构造时都会被执行。
  //构造器的执行顺序：
  //调用超类的构造器；
  //特征构造器在超类构造器之后、类构造器之前执行；
  //特征由左到右被构造；
  //每个特征当中，父特征先被构造
  //如果多个特征共有一个父特征，父特征不会被重复构造
  //所有特征被构造完毕，子类被构造
  override def A(x: Any): Boolean = {if(1 > 2) true else false}

  override def B(x: Any): Boolean = {if(1 < 2) true else false}
}
