package com.bigdata;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * 将spout发过来的手机名称转为大写
 * @author xiaxing
 *
 */
public class UpperBolt extends BaseRichBolt{

    private OutputCollector collect;
    /**
     * 是bolt组件的业务处理方法  不断地被worker的executor线程调用
     * 没收到一个消息tuple 就会调用一次
     * Tuple tuple   上一个组件发过来的消息
     * BasicOutputCollector collect  用来发送数据
     */
    public void execute(Tuple tuple) {
        // TODO Auto-generated method stub
        String phone = tuple.getStringByField("phone-name");

        //添加业务逻辑
        String upperCasePhone = phone.toUpperCase();

        //封装消息发送
        collect.emit(new Values(upperCasePhone));
    }

    public void prepare(Map config, TopologyContext context, OutputCollector collect) {
        // TODO Auto-generated method stub
        this.collect = collect;

    }

    public void declareOutputFields(OutputFieldsDeclarer declare) {
        // TODO Auto-generated method stub
        declare.declare(new Fields("upper-phone-name"));
    }

}
