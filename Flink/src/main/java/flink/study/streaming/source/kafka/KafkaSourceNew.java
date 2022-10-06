package flink.study.streaming.source.kafka;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * :Description:Flink读取Kafka作为数据源
 * :Author: 佳境Shmily
 * :Create Time: 2022/5/15 21:17
 * :Site: shmily-qjj.top
 * kafka-console-producer --broker-list cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092 --topic flink_topic1
 */
public class KafkaSourceNew {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        String topicName = "flink_topic1";
        String groupName = "test";
        String brokers = "cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092";
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(brokers)
                .setTopics(topicName)
                .setGroupId(groupName)
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(OffsetsInitializer.latest())
                .build();

        DataStreamSource<String> ds = env.fromSource(source, WatermarkStrategy.noWatermarks(),"KafkaSource");

        ds.print();

        env.execute();
    }
}
