package top.shmily_qjj.iceberg.table.maintenance

object Test {
  def main(args: Array[String]): Unit = {
    val filtersString: String = "a=1,b=2,c=3"
    filtersString.split(",").foreach(kv =>{
      val key: String = kv.split("=")(0)
      val value: String = kv.split("=")(1)
      println(key + "===" + value)
    })


    val supportedOperator = ("=", ">", "<", ">=", "<=")

      supportedOperator.productIterator.foreach(x=>println(x))

  }

}
