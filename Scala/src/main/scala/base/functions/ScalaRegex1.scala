
object ScalaRegex1 {
  def main(args: Array[String]): Unit = {
    val sql:String = "ALTER TABLE  qjj.qjj DROP partition( dt = 222,dt=dwdwd )"
//    val sql:String = "alter table qjj.qjj rename to dadadwad.dwd"
    // 匹配到直接返回提取到的结果
    sql match {
      case DDL.AlterTableRename(t1,t2) =>
        println(t1+ "  - "+t2)
      case DDL.AlterTableDropPartition(t3) =>
        println(t3)
      case _ => println(sql)
    }
  }

}


object DDL {
  val AlterTableRename ="""[\s]*alter[\s]+table[\s]+([\w\.]+)[\s]+rename[\s]+to[\s]+([\w\.]+)[\s]*""".r
  val AlterTableDropPartition ="""(?i)[\s]*alter[\s]+table[\s]+([\w\.]+)[\s]+drop[\s]+partition[\s\w\\(\\)=,]+""".r
}
