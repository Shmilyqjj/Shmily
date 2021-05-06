package com.weichuang;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class CounterInterceptor implements ProducerInterceptor<String,String> {
    private int sucessNum = 0;
    private int errorNum = 0;
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if(exception == null){
           sucessNum++;
        }else{
            errorNum++;
        }
    }

    @Override
    public void close() {
        System.out.println(sucessNum);
        System.out.println(errorNum);
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
