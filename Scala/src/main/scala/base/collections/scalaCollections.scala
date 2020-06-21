package base.collections

/**
 * Scala集合分为可变集合和不可变集合
 * 可变集合可以在适当的地方被更新或扩展。这意味着你可以修改，添加，移除一个集合的元素。而不可变集合类永远不会改变。
 * 不过，你仍然可以模拟添加，移除或更新操作。但是这些操作将在每一种情况下都返回一个新的集合，同时使原来的集合不发生改变。
 */

object scalaCollections {
  def main(args: Array[String]): Unit = {
    val l = List(1,2,3,4,1)
    l.foreach(print)
    println()
    val s = Set(1,2,3,4,1)
    s.foreach(println(_))
    val m = Map(1->"One", 2->"two", 3->"three")
    m.foreach(println)
    val o: Option[Int] = Some(5) // Some[A]是一定有值的可以用.get获取  Option可以有Some和None
    o.foreach(print)
  }
}
