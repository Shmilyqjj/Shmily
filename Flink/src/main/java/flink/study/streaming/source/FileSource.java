package flink.study.streaming.source;

import flink.study.streaming.source.pojo.Event;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * :Description: Flink读取日志文件作为数据源
 * :Author: 佳境Shmily
 * :Create Time: 2022/5/15 20:51
 * :Site: shmily-qjj.top
 */
public class FileSource {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String filePath = "D:\\Projects\\MyProjects\\IdeaProjects\\Shmily\\Flink\\input_data\\user-clicks.log";
        DataStreamSource<String> userClicksStream = env.readTextFile(filePath);
        // 转换为泛型为POJO类的流
        SingleOutputStreamOperator<Event> events = userClicksStream
                .map(line -> new Event(line.split(",")[0], line.split(",")[1], Long.parseLong(line.split(",")[2])))
                .returns(TypeInformation.of(Event.class));

        events.print("sink1");

        env.execute();
    }
}
