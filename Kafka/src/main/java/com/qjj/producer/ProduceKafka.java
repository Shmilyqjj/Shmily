package com.qjj.producer;

import com.qjj.common.PropertyUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Description: kafka生产者
 * CreateTime: 2025/4/23 14:44
 * Author Shmily
 * Usage: java -cp Kafka-1.0-SNAPSHOT-jar-with-dependencies.jar com.qjj.producer.ProduceKafka --servers "localhost:9092" --topic "rcv4" --input "/home/shmily/Desktop/rcv4.bin"
 */
public class ProduceKafka {
    public static void main(String[] args) {
        Map<String, String> params = PropertyUtils.parseArgs(args);
        String bootstrapServers = params.get("servers");
        String topic = params.get("topic");
        String input = params.get("input");
        binaryFileToKafka(bootstrapServers, topic, input);
    }

    public static void binaryFileToKafka(String bootstrapServers, String topic, String filePath) {
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", bootstrapServers);
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producerProps.put("max.request.size", "10485760");
        producerProps.put("max.partition.fetch.bytes", "12582912");
        KafkaProducer<byte[], byte[]> producer = new KafkaProducer<>(producerProps);

        File file = new File(filePath);
        if (file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] data = new byte[fis.available()];;
                fis.read(data);
                fis.close();
                ProducerRecord<byte[], byte[]> record = new ProducerRecord<>(topic, null, data);

                producer.send(record, (metadata, exception) -> {
                    if (exception == null) {
                        System.out.println("发送成功: " + file.getName());
                        file.delete(); // 可选：发送成功后删除文件
                    } else {
                        System.err.println("发送失败: " + file.getName());
                        exception.printStackTrace();
                    }
                });
            } catch (IOException e) {
                System.err.println("文件读取失败: " + file.getName());
            }
        }
        producer.flush();
        producer.close();

    }
}
