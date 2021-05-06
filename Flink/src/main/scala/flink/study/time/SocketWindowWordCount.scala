package flink.study.time

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * :Description: Flink streaming demo to test ProcessingTime & IngestionTime.
 * :Author: 佳境Shmily
 * :Create Time: 2021/3/6 14:11
 * :Site: shmily-qjj.top
 *
 * flink处理任务流程如下:
 *  ① 获取执行环境 （Environment）
 * ​ ② 加载或者创建数据源（source）
 * ​ ③ 转化处理数据（transformation）
 * ​ ④ 输出目的端（sink）
 * ​ ⑤ 执行任务（execute）
 */
object SocketWindowWordCount {
  def main(args: Array[String]):Unit = {
    // 监听的host port
    val port: Int = try {
      ParameterTool.fromArgs(args).getInt("port",8888)
    } catch {
      case e: Exception => {
        System.err.println("No port specified. Please run 'SocketWindowWordCount --port <port>'")
        return
      }
    }
    val host:String = try{
      ParameterTool.fromArgs(args).get("host","cdh101")
    }catch {
      case e:Exception => {
        System.err.println("No host specified. Please run 'SocketWindowWordCount --host <host>'")
        return
      }
    }
    println(s"Listening host:$host port:$port")

    //获取 流数据源 执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //将时间特性设置为ProcessingTime或IngestionTime  默认是EventTime  env.getStreamTimeCharacteristic
    // EventTime一定要有事件发生的时间戳否则报错  EventTime要求事件本身携带时间参数
    // 绝大部分业务使用EventTime 只有EventTime不可用时才被迫使用提取时间和处理时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)
    // 通过nc -lk 8888 产生数据 flink监听该节点的8888端口
    val text:DataStream[String] = env.socketTextStream(host,port,'\n')
    // 解析数据, 分组, 窗口化, 并且聚合求SUM
    val dataStream = text.flatMap(_.split(" "))
      .map((_,1))
      .keyBy(0)
      .timeWindow(Time.seconds(5),Time.seconds(5))
      .sum(1)

    // 打印输出并设置使用一个并行度
    dataStream.print().setParallelism(1)
    env.execute("Socket Window WordCount")
  }

  def testEventTime():Unit={

  }
}
