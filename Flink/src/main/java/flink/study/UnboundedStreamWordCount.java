package flink.study;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * :Description:
 * :Author: 佳境Shmily
 * :Create Time: 2022/5/13 14:45
 * :Site: shmily-qjj.top
 */
public class UnboundedStreamWordCount {
    public static void main(String[] args) throws Exception {
        // 生成流式env环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 获取程序参数  --host cdh101 --port 8888
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String host = parameterTool.get("host","cdh101");
        int port = parameterTool.getInt("port", 8888);

        // 读数据源  (nc -lk 8888)
        DataStreamSource<String> dataStreamSource = env.socketTextStream(host, port);

        //逻辑  得到单一输出流算子
        SingleOutputStreamOperator<Tuple2<String, Long>> sum = dataStreamSource
                .flatMap((String line, Collector<String> words) -> Arrays.stream(line.split(" ")).forEach(words::collect))
                .returns(Types.STRING)
                .map(x -> Tuple2.of(x, 1L))
                .returns(Types.TUPLE(Types.STRING,Types.LONG))
                .keyBy(data -> data.f0)
                .sum(1);


        //输出
        sum.print();

        //触发执行  持续等待数据到来
        env.execute();



    }
}
