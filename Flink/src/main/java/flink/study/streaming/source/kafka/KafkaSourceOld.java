package flink.study.streaming.source.kafka;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

/**
 * :Description:Flink读取Kafka作为数据源
 * :Author: 佳境Shmily
 * :Create Time: 2022/5/15 21:17
 * :Site: shmily-qjj.top
 * kafka-console-producer --broker-list cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092 --topic flink_topic1
 */
public class KafkaSourceOld {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 添加Source  连接Kafka需要flink-connector-kafka连接器
        // SourceFunction包含run()和cancel()方法 run负责不断连接数据源和读取数据源 cancel负责在一定条件下停止读取数据源
        // flink-connector-kafka已经实现了所需的SourceFunction 直接调用
        String topicName = "flink_topic1";
        Properties kafkaConnProperties = new Properties();
        kafkaConnProperties.setProperty("bootstrap.servers", "cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092");
        kafkaConnProperties.setProperty("group.id", "flink-source-cg");
        kafkaConnProperties.setProperty("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConnProperties.setProperty("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConnProperties.setProperty("auto.offset.reset", "latest");

        // 连接kafka数据源
        DataStreamSource<String> kafkaStreamSource = env.addSource(new FlinkKafkaConsumer<String>(topicName, new SimpleStringSchema(), kafkaConnProperties));

        kafkaStreamSource.print();

        env.execute();
    }
}
