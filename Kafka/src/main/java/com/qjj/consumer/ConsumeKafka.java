package com.qjj.consumer;

import com.qjj.common.PropertyUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Description: 消费kafka数据到文件
 * CreateTime: 2025/4/22 18:04
 * Author Shmily
 * Usage: java -cp Kafka-1.0-SNAPSHOT-jar-with-dependencies.jar com.qjj.consumer.ConsumeKafka --servers "localhost:9092" --topic "rcv4" --group "test" --limit 10 --output "/tmp/rcv4.bin"
 */
public class ConsumeKafka {
    public static void main(String[] args) {
        // 解析命令行参数
        Map<String, String> params = PropertyUtils.parseArgs(args);

        // 获取参数值
        String servers = params.get("servers");
        String topic = params.get("topic");
        String group = params.get("group");
        Long limit = Long.parseLong(params.getOrDefault("limit", "10"));
        String output = params.getOrDefault("output", "/tmp/test.txt");
        consumeBinaryToFile(servers, topic, group, limit, output);
    }

    public static void consumeBinaryToFile(String bootstrapServers, String topic, String groupId, Long limit, String outputFilePath) {
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", bootstrapServers);
        consumerProps.put("group.id", groupId);
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");

        KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(topic));
        System.out.printf("Starting to consume from topic %s%n", topic);
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            long consumedCnt = 0L;
            while (limit < 0 || consumedCnt <= limit) {
                ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<byte[], byte[]> record : records) {
                    consumedCnt ++;
                    fos.write(record.value());
                }
            }
            fos.flush();
            consumer.commitSync();
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("consumeBinaryToFile finished.");
    }

}
