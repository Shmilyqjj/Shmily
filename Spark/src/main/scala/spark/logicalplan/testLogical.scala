package spark.logicalplan
import org.apache.commons.lang3.StringUtils
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.catalyst.analysis.UnresolvedRelation
import org.apache.spark.sql.catalyst.plans.logical.{Aggregate, Deduplicate, DeserializeToObject, Distinct, Filter, Generate, GlobalLimit, InsertAction, Join, LocalLimit, LogicalPlan, MapElements, MapPartitions, Project, Repartition, RepartitionByExpression, SerializeFromObject, SubqueryAlias, TypedFilter, Union, Window}
import org.apache.spark.sql.execution.datasources.CreateTable




object testLogical {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "D:\\Applications\\hadoop-3.0.0\\")
    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    val sc:SparkContext = new SparkContext(conf)
    sc.setLogLevel("WARN")
    testLogicalPlan(sc)



  }

  def testLogicalPlan(sc:SparkContext): Unit ={
    val sqlContext = new SQLContext(sc)
    val logicalPlan = sqlContext.sparkSession.sessionState.sqlParser.parsePlan("select distinct * from aaa where cust_no not in(select 1 from bbb limit 3) limit 3000")
    println(resolveLogicalPlan(logicalPlan))

  }

  /**
   * resolve Filter operation in logicalPlan to check "not in"
   * @param logicalPlan
   */

  def resolveLogicalPlan(logicalPlan:LogicalPlan):Boolean ={
    logicalPlan match {
      case project: Project => resolveLogicalPlan(project.child)
      case globalLimit: GlobalLimit => resolveLogicalPlan(globalLimit.child)
      case localLimit: LocalLimit => resolveLogicalPlan(localLimit.child)
      case createTable: CreateTable => resolveLogicalPlan(createTable.query.get)
      case distinct: Distinct => resolveLogicalPlan(distinct.child)
      case subqueryAlias: SubqueryAlias => resolveLogicalPlan(subqueryAlias.child)
//      case insertIntoTable: InsertAction =>
//        resolveLogicalPlan(insertIntoTable.ch)
//        resolveLogicalPlan(insertIntoTable.query)
      case join: Join =>
        resolveLogicalPlan(join.left)
        resolveLogicalPlan(join.right)
      case aggregate: Aggregate => resolveLogicalPlan(aggregate.child)
      case union: Union => for (child <- union.children) {resolveLogicalPlan(child)}
      case filter: Filter =>
        if(filter.toString().contains("Filter")) {
          val filterJSON = filter.toJSON
          println(filterJSON)
          resolveFilterLogicalJson(filterJSON, "")
          return false
        }else{
          println(filter.toString.replace("\n"," "))
          resolveLogicalPlan(filter.child)
        }
      case unresolvedRelation: UnresolvedRelation => "unresolvedRelation"
      case _ => println("Other type logical plan that we do not care about."+logicalPlan.toString())
    }
    return true
  }

  def resolveFilterLogicalJson(filterJSON:String, rules:String): Unit ={
    import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
    val jsonOBJ: JSONObject  = JSON.parseObject(filterJSON)
    val result: JSONArray = jsonOBJ.getJSONArray("resultData")
    println(result)


  }

}
