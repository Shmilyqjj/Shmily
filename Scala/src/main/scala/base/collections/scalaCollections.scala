package base.collections

/**
 * References: https://www.cnblogs.com/duanxz/p/9460656.html
 */

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Scala集合分为可变集合和不可变集合
 * 可变集合可以在适当的地方被更新或扩展。这意味着你可以修改，添加，移除一个集合的元素。而不可变集合类永远不会改变。
 * 不过，你仍然可以模拟添加，移除或更新操作。但是这些操作将在每一种情况下都返回一个新的集合，同时使原来的集合不发生改变。
 */

object scalaCollections {
  def main(args: Array[String]): Unit = {
    /**
     * List列表
     */
    val l = List(1,2,3,4,1)
    val ll: List[Int] = List(1,2,3,4)
    val ln: List[Nothing] = List()
    ln.foreach(print)
    l.foreach(print)
    println()
    println("*************************************************")
    /**
     * Set集合
     * 默认情况下，Scala 使用的是不可变集合，如果想使用可变集合，需引用 scala.collection.mutable.Set 包。
     * 默认引用 scala.collection.immutable.Set。
     */
    val s = Set(1,2,3,4,1)
    val ss: Set[Int] = Set(1,2,3,4)
    s.foreach(println(_))
    println(s.exists(_ % 2 == 1))
    val sss = s.drop(1)
    println(sss)
    val m = Map(1->"One", 2->"two", 3->"three")
    m.foreach(println)
    println()
    //可变Set  对不可变Set进行操作，会产生一个新的set，原来的set并没有改变，这与List一样。
    //而对可变Set进行操作，改变的是该Set本身，与ListBuffer类似。
    val ms = scala.collection.mutable.Set(1,2,3)
    ms.add(4)
    ms.remove(1)
    ms += 5
    ms -= 3
    println(ms)
    //两个Set相连接
    val sl = ms ++ mutable.Set(4,5,6)
    println(sl)
    println(sl.min)
    println("*************************************************")

    //二维列表
    val dl: List[List[Int]] = List(List(1,2),List(3,4))
    println()

    //空列表
    val nullList: List[Nothing] = Nil //Nil代表空列表  等同于List[Nothing]
    val addToList: List[String] = "hahaha " :: ("zxw " :: ("qjj " :: Nil))  //双冒号是追加进入
    addToList.foreach(print)

    /**
     * Scala类型系统中Nil, Null, None, Nothing四个类型的区别
     * Nothing
     * Nothing是所有类型的子类，它没有对象，但是可以定义类型，如果一个类型抛出异常，那这个返回值类型就是Nothing
     *
     * Null
     * Null是AnyRef的子类（引用类型） null是Null唯一的对象
     *
     * None
     * None是Option的一个子类，一个Some集合，一个None，如果Option中没有值，则返回None
     *
     * Nil
     * Nil是一个空List，定义为List[Nothing],所有Nil是所有List[T]的子类
     */


    println("打印Scala列表的头部元素：" + l.head + " 得到除了第一元素之外的其他元素：（是一个列表或者Nil）" + l.tail)
    println(Nil.isEmpty)
    println("*************************************************")

    //两个List相连接
    val list_a = List(1,2)
    val list_b = List(3,4)
    var list_c = list_a ::: list_b  // 可以使用  :::  、 List.:::()  或  List.concat()  方法来连接两个或多个列表
    list_c.foreach(print(_))
    println()
    val list_d = list_a.:::(list_b)  // a合并到b后面
    list_d.foreach(print)
    println()
    val list_e = List.concat(list_a,list_b)
    list_e.foreach(print)
    println()
    println("*************************************************")

    //创建重复数据的数组
    val lc: List[String] = List.fill(5)("qjj")
    lc.foreach(print)
    println()
    println("*************************************************")

    //ListBuffer列表缓存
    val buf: ListBuffer[Int] = new ListBuffer[Int]
    buf += 1
    buf += 2
    buf.toList.foreach(println)
    println(buf.toString())
    println(buf.toList.toString())
    println("*************************************************")

    /**
     * 元组Tuple
     * 与列表一样，元组也是不可变的，但与列表不同的是元组可以包含不同类型的元素。(List[Any]可以包含不同类型元素）
     */
    val tuple = (1, 2,"qjj", "zxw", 5.20)
    println(tuple._1)
    tuple.productIterator.foreach(println)//可以使用 Tuple.productIterator() 方法来迭代输出元组的所有元素
    //只有两个元素的tuple交换元素
    (1,2).swap.productIterator.foreach(println)
    //模式匹配获取元组元素
    val (a,b,c,d,e) = tuple
    println(a + "," + b + "," + c)
    println("*************************************************")

    /**
     * 队列
     * 如果你需要先进先出序列，你可以使用Queue(队列)。Scala集合提供了可变和不可变的Queue。
     */
    var queue = mutable.Queue[Int]()  //可变queue
    queue.enqueue(1,2,3)  //使用enqueue为队列添加元素
    val ele,ele1,ele2 = queue.dequeue()
    println(ele)
    println(ele2)
    queue += 1  // += 添加元素到队列
    queue ++= List(2,3,4) // ++=添加多个元素
    queue += 5
    println(queue)
    println(queue.dequeue)  //出队 第一个元素

    val ImmutableQueue = collection.immutable.Queue(1,2,3)
    val ImmutableQueue1 = ImmutableQueue.enqueue(4)  //为不可变队列添加元素 - 产生新集合
    println(ImmutableQueue1)
    println("*************************************************")

    /**
     * 栈
     * 如果需要的是后进先出，你可以使用Stack，它同样在Scala的集合中有可变和不可变版本。
     * 元素的推入使用push，弹出用pop，只获取栈顶元素而不移除可以使用top。
     */
    //可变栈
    val stack = scala.collection.mutable.Stack(1,2,3,4)
    stack.push(5)
    stack.push(6) //压栈
    println(stack)
    println(stack.top) //获取当前栈顶元素（不出栈）
    println(stack.pop()) //出栈栈顶元素
    println(stack)


    //不可变栈
    val ImmutableStack = scala.collection.immutable.Stack()
    println("*************************************************")

    /**
     * Map  k-v集合
     */
    val map0 = Map(1 -> "qjj", 2 -> "xww")
    println(map0.isEmpty)
    var map1 = Map[Int, String](1 -> "qjj", 2 -> "zxw")
    map1.toParArray.foreach(x => println(x._1 + 1,x._2))
    map1 += (3 -> "xww")  //添加元素
    println(map1)
    println(map1.keys)  //所有key的Set集合
    println(map1.values) //所有Value
    //可变Map
    val im = scala.collection.mutable.Map(1->"qjj",2->"zxw")
    im(2) = "xww" //给可变map的元素赋值
    println(im)
    //Map合并
    val map2 = map0 ++ map1
    println(map2)
    //输出Map的k，v
    map0.keys.foreach(x => println("key:" + x + " ,value:" + map0(x)))
    //输出Map的k，v
    for((k,v) <- map0){
      println(k + ":" + v)
    }
    //查看是否存在指定key
    println(map0.contains(3) + "," + map0.contains(1))
    //反转k，v
    val map3 = for((k,v) <- map0) yield (v,k);println(map3)

    println("*************************************************")

    /**
     * 拉链操作  zip
     * 两个元素个数相同的List变一个Map映射
     */
    val out = List(1,2,3).zip(List("First","Second","Third"))
    println(out)
    println(out.toMap)

    /**
     * Option
     * Scala Option(选项)类型用来表示一个值是可选的（有值或无值)。
     * Option[T] 是一个类型为 T 的可选值的容器： 如果值存在， Option[T] 就是一个 Some[T] ，如果不存在， Option[T] 就是对象 None 。
     * Option 有两个子类别，一个是 Some，一个是 None，当他回传 Some 的时候，代表这个函式成功地给了你一个 String，而你可以透过 get() 这个函式拿到那个 String，如果他返回的是 None，则代表没有字符串可以给你。
     */
    val o: Option[Int] = Some(5) // Some[A]是一定有值的可以用.get获取  Option可以有Some和None
    val on: Option[Int] = None
    o.foreach(println)
    val myMap: Map[String, String] = Map("key1" -> "value")
    val value1: Option[String] = myMap.get("key1")
    val value2: Option[String] = myMap.get("key2")
    println(value1)
    println(value2)
    println(o.getOrElse(0) + " " + on.getOrElse(0))//使用getOrElse方法来获取Option存在的元素或者使用其默认的值
    println(o.isEmpty + " " + on.isEmpty) //判断Option是否有值
    println("*************************************************")

    /**
     * Scala迭代器
     * Scala Iterator（迭代器）不是一个集合，它是一种用于访问集合的方法。
     * 迭代器 it 的两个基本操作是 next 和 hasNext。
     * 调用 it.next() 会返回迭代器的下一个元素，并且更新迭代器的状态。
     * 调用 it.hasNext() 用于检测集合中是否还有元素。
     */
    val it = Iterator(1,2,3,4,5)
    while(it.hasNext){
      print(it.next() + " ")
    }
    println()
    println(it.length + " " + it.size)  //获取迭代器元素个数  可知经过上面遍历，迭代器为空


  }
}
