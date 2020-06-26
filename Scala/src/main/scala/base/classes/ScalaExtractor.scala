package base.classes

/**
 * :Description:Scala提取器Extractor
 * :Author: 佳境Shmily
 * :Create Time: 2020/6/26 12:24
 * :Site: shmily-qjj.top
 *
 * Scala提取器：提取器是从传递给它的对象中提取出构造该对象的参数。
 * Scala 提取器是一个带有unapply方法的对象。
 * unapply方法算是apply方法的反向操作：unapply接受一个对象，然后从对象中提取值，提取的值通常是用来构造该对象的值。
 *
 */
object ScalaExtractor {
  def main(args: Array[String]) {

    println ("Apply 方法 : " + apply("Zara", "gmail.com"));
    println ("Unapply 方法 : " + unapply("Zara@gmail.com"));
    println ("Unapply 方法 : " + unapply("Zara Ali"));

  }
  // 注入方法 (可选)
  def apply(user: String, domain: String) = {  //通过 apply 方法 无需使用 new 操作就可以创建对象。
    user +"@"+ domain
  }

  // 提取方法（必选）
  def unapply(str: String): Option[(String, String)] = {  //实例中 unapply 方法在传入的字符串不是邮箱地址时返回 None
    val parts = str split "@"
    if (parts.length == 2){
      Some(parts(0), parts(1))
    }else{
      None
    }
  }
}

