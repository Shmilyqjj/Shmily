package com.qjj.consumer;

import com.qjj.common.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * Description: 消费kafka数据到文件
 * CreateTime: 2025/4/22 18:04
 * Author Shmily
 * Usage: java -cp Kafka-1.0-SNAPSHOT-jar-with-dependencies.jar com.qjj.consumer.ConsumeKafka --servers "localhost:9092" --topic "rcv4" --group "test" --limit 10 --output "/tmp/rcv4.bin"
 * Usage[SSL]: java -cp Kafka-1.0-SNAPSHOT-jar-with-dependencies.jar com.qjj.consumer.ConsumeKafka --servers "localhost:9092" --topic "rcv4" --group "test" --limit 10 --output "/tmp/rcv4.txt" --ssl-enabled true --trust-store-location "/home/shmily/.truststore.jks" --trust-store-password KafkaOnsClient --jaas-username xxx --jaas-password xxx
 * Usage[SSL]: java -Djava.security.auth.login.config=/path/to/kafka_client_jaas.conf=/path/to/kafka_client_jaas.conf -cp Kafka-1.0-SNAPSHOT-jar-with-dependencies.jar com.qjj.consumer.ConsumeKafka --servers "localhost:9092" --topic "rcv4" --group "test" --limit 10 --output "/tmp/rcv4.txt" --ssl-enabled true --trust-store-location "/home/shmily/.truststore.jks" --trust-store-password KafkaOnsClient
 * Usage[SSL]: java -cp Kafka-1.0-SNAPSHOT-jar-with-dependencies.jar com.qjj.consumer.ConsumeKafka --servers "localhost:9092" --topic "rcv4" --group "test" --limit 10 --output "/tmp/rcv4.txt" --ssl-enabled true --trust-store-location "/home/shmily/.truststore.jks" --trust-store-password KafkaOnsClient --jaas-config /path/to/kafka_client_jaas.conf
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
        boolean sslEnabled = "true".equals(params.get("ssl-enabled"));
        if (sslEnabled) {
            String trustStoreLocation = params.get("trust-store-location");
            String trustStorePassword = params.get("trust-store-password");
            consumeSslKafka(servers, topic, group, trustStoreLocation,
                    trustStorePassword,params.get("jaas-config"),
                    params.get("jaas-username"), params.get("jaas-password"), limit, output);
        } else {
            consumeBinaryToFile(servers, topic, group, limit, output);
        }
    }

    /**
     * 消费ssl kafka
     * @param bootstrapServers brokers
     * @param topic topic
     * @param groupId group
     * @param trustStoreLocation truststore location
     * @param trustStorePassword truststore password
     * @param jaasConfigLocation jaas config location
     * @param jaasUserName jaas username
     * @param jaasPassword jaas password
     * @param limit number of messages to consume
     * @param outputFilePath output file
     */
    public static void consumeSslKafka(String bootstrapServers,
                                       String topic,
                                       String groupId,
                                       String trustStoreLocation,
                                       String trustStorePassword,
                                       String jaasConfigLocation,
                                       String jaasUserName,
                                       String jaasPassword,
                                       Long limit,
                                       String outputFilePath) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", groupId);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // ssl
        if (System.getProperty("java.security.auth.login.config") != null) {
            // 通过JVM参数指定-Djava.security.auth.login.config=/path/to/kafka_client_jaas.conf
            System.out.println("jaas was set by jvm args");
        } else if (StringUtils.isNotEmpty(jaasConfigLocation)) {
            System.setProperty("java.security.auth.login.config", jaasConfigLocation);
        } else if (StringUtils.isNotEmpty(jaasUserName) && StringUtils.isNotEmpty(jaasPassword)) {
            props.put("sasl.jaas.config", String.format("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",jaasUserName, jaasPassword));
        } else {
            System.out.println("please set one of [-Djava.security.auth.login.config] [--jaas-config] [--jaas-username and --jaas-password]. ");
            System.exit(1);
        }
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStoreLocation);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, trustStorePassword);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        List<String> subscribedTopics =  new ArrayList<String>();
        subscribedTopics.add(topic.trim());
        consumer.subscribe(subscribedTopics);

        consumer.subscribe(Collections.singletonList(topic));
        System.out.printf("Starting to consume from topic %s%n", topic);

        try (FileWriter writer = new FileWriter(outputFilePath)) {
            long consumedCnt = 0L;
            while (limit < 0 || consumedCnt <= limit) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    consumedCnt ++;
                    writer.write(record.value());
                }
            }
            writer.flush();
            consumer.commitSync();
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("consumeBinaryToFile finished.");
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
