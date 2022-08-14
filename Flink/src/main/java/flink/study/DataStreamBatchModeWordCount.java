package flink.study;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;

/**
 * :Description:Flink DataStream API 批处理 通过ExecutionMode设置
 * :Author: 佳境Shmily
 * :Create Time: 2021/3/12 15:32
 * :Site: shmily-qjj.top
 * args: -Dexecution.runtime-mode=BATCH  用批处理方式运行  默认是STREAMING方式
 * 或者env.setRuntimeMode(RuntimeExecutionMode.BATCH); 设置该程序为批处理模式
 */
public class DataStreamBatchModeWordCount {
    public static void main(String[] args) throws Exception {
        //1.创建Env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);  // 设置为批处理

        //2.读文件 得到数据源  
        DataStreamSource<String> fileDataSource = env.readTextFile("D:\\Projects\\MyProjects\\IdeaProjects\\Shmily\\Flink\\input_data\\words.txt");

        //3.逻辑 数据扁平化打散flatMap得到单词word，word转为二元组(Tuple2)，统计元组
        // line->word->Tuple(word,1)->sum
        SingleOutputStreamOperator<Tuple2<String, Long>> sum = fileDataSource
                .flatMap((String line, Collector<String> words) -> Arrays.stream(line.split(" ")).forEach(words::collect))
                .returns(Types.STRING)
                .map(x -> Tuple2.of(x, 1L))
                .returns(Types.TUPLE(Types.STRING,Types.LONG))
                .keyBy(data -> data.f0)
                .sum(1);


        //4 打印
        sum.print();

        env.execute();
    }
}
