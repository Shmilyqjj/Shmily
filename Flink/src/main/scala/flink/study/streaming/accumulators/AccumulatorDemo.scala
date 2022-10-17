package flink.study.streaming.accumulators

import org.apache.flink.api.common.JobExecutionResult
import org.apache.flink.api.common.functions.{RichMapFunction, RuntimeContext}
import org.apache.flink.api.common.accumulators.{DoubleCounter, IntCounter, LongCounter}
import org.apache.flink.configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.hadoop.conf.Configuration

/**
 * Flink 累加器  累加统计结果
 * Test on manjaroLinux:sudo pacman -S gnu-netcat;nc -l -p 9999
 * Test on CentOS:sudo yum install nmap-ncat;nc -lk 9999
 * 输入数据
  1,user1,20221017114253,32.3
  2,user2,20221017114253,37.1
  3,user3,20221017114312,38.0
  4,user4,20221017114328,35.4
  5,user5,20221017114344,33.8
  6,user6,20221017115122,34.1
 */
object AccumulatorDemo {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val hostName="localhost"
    val port=9999
    val ds: DataStream[String] = env.socketTextStream(hostName, port)

    ds.map(
      new RichMapFunction[String, String] {
        //1、声明累加器，要在富函数中声明全局的
        val normal = new LongCounter()
        val exception = new LongCounter()
        val all = new LongCounter()

        override def open(parameters: configuration.Configuration): Unit = {
          // 添加累加器
          val rc: RuntimeContext = getRuntimeContext
          rc.addAccumulator("normal_temperture",normal)
          rc.addAccumulator("exception_temperture",exception)
          rc.addAccumulator("all_temperture",all)
        }


        // 3.进行累加
        override def map(value: String): String = {
          //进行累加
          all.add(1)
          val temper: Double = value.split(",")(3).toDouble
          if (temper > 37.0){
            //进行累加
            exception.add(1)
            s"${value.split(',')(1).trim} 的体温为 $temper, 体温过高 超过37.0度"
          }else{
            //进行累加
            normal.add(1)
            s"${value.split(',')(1).trim} 的体温为 $temper, 体温正常，请进入"
          }
        }
      }
    ).print("Stream结果: ")

    val result: JobExecutionResult = env.execute("testAccumulatorJob")

    //4.执行完了之后获取累加器的值
    val normal: Long = result.getAccumulatorResult[Long]("normal_temperture")
    val exception: Long = result.getAccumulatorResult[Long]("exception_temperture")
    val all: Long = result.getAccumulatorResult[Long]("all_temperture")

    println(s"normal $normal")
    println(s"exception $exception")
    println(s"all $all")
  }

}
