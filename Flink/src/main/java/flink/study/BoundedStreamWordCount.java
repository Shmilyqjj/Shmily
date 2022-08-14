package flink.study;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import java.util.Arrays;

/**
 * :Description: DataSet API以后都会弃用，Flink统一使用DataStreamAPI来处理流&批数据，API统一。
 * :Author: 佳境Shmily
 * :Create Time: 2022/5/13 11:53
 * :Site: shmily-qjj.top
 * : BoundedStream 有界流处理 读取一个文件就是有界流
 *
 * 10> (flink,1)   10> 线程id是10
 * 4> (hello,1)
 * 1> (spark,1)
 * 4> (hello,2)
 *
 */
public class BoundedStreamWordCount {
    public static void main(String[] args) throws Exception {
        // 生成流式env环境  getExecutionEnvironment本地执行时获得的是一个虚拟环境  提交生产环境执行时获取到的是真实的远程集群环境
        // getExecutionEnvironment是一种自适应的获取环境的方式，其底层是createLocalEnvironment和createRemoteEnvironment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 连接远程的Env  比如远程启动了yarn-session http://cdh102:44935  createRemoteEnvironment三个参数host port和要提交运行的jar的路径
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("cdh102", 44935, "D:\\Projects\\MyProjects\\IdeaProjects\\Shmily\\Flink\\target\\Flink-1.0-SNAPSHOT.jar");
        env.setParallelism(2);  // env级别并行度设置(不推荐)
//        env.disableOperatorChaining(); // 全局禁用算子链合并

        // 读数据源
//        DataStreamSource<String> fileDataStream = env.readTextFile("D:\\Projects\\MyProjects\\IdeaProjects\\Shmily\\Flink\\input_data\\words.txt");  //本地模式可读取本地数据源 Remote模式要使用远程数据源或者hdfs上的文件
        DataStreamSource<String> fileDataStream = env.readTextFile("hdfs://cdh102:8020/data/data_test/words.txt");

        //逻辑  得到单一输出流算子
        SingleOutputStreamOperator<Tuple2<String, Long>> sum = fileDataStream
                .flatMap((String line, Collector<Tuple2<String, Long>> out) -> Arrays.stream(line.split(" ")).forEach(word -> out.collect(Tuple2.of(word, 1L))))
                .returns(Types.TUPLE(Types.STRING, Types.LONG))
                //.keyBy(0)
                .keyBy(data -> data.f0)  // 取第一个字段 代替过期API（keyBy(0)）
                .sum(1)
//                .slotSharingGroup("1")  // 设置slot共享组 算子后接slotSharingGroup  设置为相同共享组的算子可以共享TaskSlot 不是同一个共享组的算子不能共享TaskSlot，必须隔离 如果设置了flatMap算子的共享组是1，那它后面的算子默认跟前面的算子一样，都属于这个共享组，不需要再重复设置slotSharingGroup
                .setParallelism(1); // 算子级别并行度设置  （算子级别的并行度优先级最高 > env设置的并行度优先级 > 提交job时设置的并行度参数-p的优先级 > flink配置文件中parallelism.default）

        //输出
        sum.print()
           .disableChaining();  // disableChaining 当前算子不做与前后两个算子的链合并操作，让每个算子的subTask都单独使用一个TaskSlot

        //触发执行  持续等待数据到来
        env.execute();



    }
}
