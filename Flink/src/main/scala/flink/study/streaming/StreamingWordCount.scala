package flink.study.streaming

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.api.java.utils.ParameterTool

/**
 * :Description:
 * :Author: 佳境Shmily
 * :Create Time: 2022/5/2 11:55
 * :Site: shmily-qjj.top
 */
object StreamingWordCount {
  def main(args: Array[String]): Unit = {
    val sockHost:String = "10.2.5.100"
    val sockPort:Int = 9999
    // 第一步获取执行环境env  （StreamExecutionEnvironment是流式处理env;ExecutionEnvironment是批处理env）
    // getExecutionEnvironment 如果本地启动则创建本地环境 如果集群上启动则创建集群环境
    // StreamExecutionEnvironment.createLocalEnvironment(6)创建6个并行度的本地环境
    // StreamExecutionEnvironment.createRemoteEnvironment 指定远程JM的IP和RPC端口以及运行程序的jar路径
    val env:StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    // 第二步 数据读取、初始化
    // 读取Socket生成流
    val text:DataStream[String] = env.socketTextStream(sockHost,sockPort)

    // 第三步 数据转换
    val counts:DataStream[(String,Int)] = text
      .flatMap(_.toLowerCase.split(" "))
      .filter(_.nonEmpty)
      .map((_,1))
      .keyBy(0)
      .sum(1)

    // 第四步 指定计算结果输出位置、方式
    val params = ParameterTool.fromArgs(args)
    if(params.has("output-file")){
      counts.writeAsText(params.get("output-file"))
    }else{
      println("Printing result to stdout.Use --output-file to specify output path")
      counts.print()
    }

    // 第五步 执行job名称并触发流式任务
    env.execute("StreamingWordCount")

  }
}
