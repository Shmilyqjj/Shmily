package base.classes

/**
  * Description:  Scala访问修饰符 private  protected  public
  * Date: 2020/5/23 20:14
  * Author: 佳境Shmily
  * Site: shmily-qjj.top
  */
object AccessModifier {
  def main(args: Array[String]) : Unit = {

  }

  // 访问修饰符  Scala不指定访问修饰符时默认为public
  // Scala中的private比 Java 更严格，在嵌套类情况下，外层类甚至不能访问被嵌套类的私有成员。
  // Scala中Protected比 Java 更严格一些(简言之：不支持同一包中访问)。因为它只允许保护成员在定义了该成员的的类的子类中被访问。而在java中，用protected关键字修饰的成员，除了定义了该成员的类的子类可以访问，同一个包里的其他类也可以进行访问。
  private val s1 = "private string"
  protected val s2 = "protected string"
  val s3 = "public string"

  private def privateMethod() : Unit = {print("privateMethod")}
  protected def protectedMethod() : Unit = {print("protectedMethod")}

  // 作用域保护  private[xxx]   protected[xx]
  class test{
    def testMethod() : Unit = {
      print("testMethod")
    }
    private[test] val a = 1  // 对test类可见 test类之外的代码都不能访问
  }




}
