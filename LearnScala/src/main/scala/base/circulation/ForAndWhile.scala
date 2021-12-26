package base.circulation

/**
  * Description: Scala for循环 while循环
  * Date: 2020/5/24 13:06
  * Author: 佳境Shmily
  * Site: shmily-qjj.top
  */
object ForAndWhile {
  def main(args: Array[String]): Unit = {
    var i = 0
    // while循环
    while(i < 10){
      i += 1
      print(i)
    }
    print("\n")

    // for循环
    for(i <- 1 to 10){  //包括1和10
      print(i)
    }
    print("\n")
    for(i <- 1 until 10){  //包括1不包括10
      print(i)
    }
    print("\n")
    for(i <- 1 to 10 if i%2 == 0){  //循环守卫
      print(i)
    }
    print("\n")
    for(i <- 1 to 10; j = 6 - i){  //循环带变量
      print(j)
    }
    print("\n")
    for(i <- 1 to 10; j<- 1 until 10){ //双重for
      print("i:" + i + ",j:" + j + ",")
    }
    println()
    for(i <- Range(1, 10, 2)){  //步长  Range是前闭后开
      print(i)
    }
    println()
    val l = List(1,2,"qjj","zxw",3)  // 遍历集合
    for(item <- l) print(item + " ")
    println()
    // do while循环
    do{
      print(i)
      i -= 1
    }while(i > 0)

  }
}
