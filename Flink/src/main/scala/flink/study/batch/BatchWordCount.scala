package flink.study.batch

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
// 解决 flink wordcount error: No implicits found for parameter evidence$11: TypeInformation[S...
import org.apache.flink.streaming.api.scala._

/**
 * :Description:Flink batch demo.
 * :Author: 佳境Shmily
 * :Create Time: 2021/3/6 17:32
 * :Site: shmily-qjj.top
 */
object BatchWordCount {
  case class UserClickLog(userName:String, eventTime:String, logType:String, pageNum:Int)

  def main(args: Array[String]): Unit = {
    // 获取 批处理 执行环境  (批处理获取执行环境用ExecutionEnvironment，流处理获取环境用StreamExecutionEnvironment)
    val env:ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    //批处理后的数据是DataSet，流处理后的数据是DataStream.
    val logs:DataSet[UserClickLog] = env
      .fromElements(
        UserClickLog("user_1", "1000", "click", 1),
        UserClickLog("user_2", "2000", "click", 2),
        UserClickLog("user_1", "1500", "click", 2)
      )
    logs.map(x=>(x.userName,1)).groupBy(0).sum(1).print()

    // 在批处理中，如果输出目的端，执行的是print命令（除此之外，还有count，collect方法），则执行任务Execute不需要调用
    // 输出目的端调用writeAsCsv、writeAsText等其他方法，则后面需要调用Execute；
//    env.execute("BatchWordCount")

  }
}
