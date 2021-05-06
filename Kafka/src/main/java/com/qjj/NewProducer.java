package com.weichuang;



import kafka.tools.ConsoleProducer;
import org.apache.kafka.clients.producer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NewProducer {

    public static void main(String[] args) {
        Properties props = new Properties();
        // 等待所有副本节点的应答
        props.put("acks","all");
        props.put("bootstrap.servers", "hadoop101:9092");
        props.put("partitioner.class","com.weichuang.KafkaPartitioner");
        // key序列化
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // value序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        List<String> interceptor = new ArrayList();
        interceptor.add("com.weichuang.TimeInterceptor");
        interceptor.add("com.weichuang.CounterInterceptor");




        props.put("interceptor.classes",interceptor);

        KafkaProducer producer = new KafkaProducer(props);
        for(int i=0;i<10;i++){
            ProducerRecord record = new ProducerRecord("second", String.valueOf(i));
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if(exception == null){
                        System.out.println(metadata.partition()+"=>>"+metadata.topic());
                    }else{
                        System.out.println("发送失败！！！");
                    }
                }
            });
        }
        producer.close();

    }
}
