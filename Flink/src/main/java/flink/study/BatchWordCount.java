package flink.study;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import scala.Int;

import java.util.Arrays;

/**
 * :Description:Flink batchMode wordCount
 * :Author: 佳境Shmily
 * :Create Time: 2021/3/6 15:32
 * :Site: shmily-qjj.top
 */
public class BatchWordCount {
    public static void main(String[] args) throws Exception {
        //1.创建Env
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        //2.读文件 得到数据源
        DataSource<String> fileDataSource = env.readTextFile("D:\\Projects\\MyProjects\\IdeaProjects\\Shmily\\Flink\\input_data\\words.txt");

        //3.逻辑 数据扁平化打散flatMap得到单词word，word转为二元组(Tuple2)，统计元组
        // line->word->Tuple(word,1)->sum
        FlatMapOperator<String, Tuple2<String, Long>> wordTuple = fileDataSource.flatMap((String line, Collector<Tuple2<String, Long>> out) -> {
            String[] words = line.split(" ");
            Arrays.stream(words).forEach(word -> out.collect(Tuple2.of(word, 1L)));
        }).returns(Types.TUPLE(Types.STRING, Types.LONG));// Java原生不支持函数式编程，为了防止泛型擦除，需要加returns方法声明返回类型

        AggregateOperator<Tuple2<String, Long>> sum = wordTuple.groupBy(0).sum(1);

        //4 打印
        sum.print();

        env.execute();
    }
}
