
package com.bigdata;

import java.util.Map;
import java.util.Random;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

/**
 * spout组件是整个topology的源组件
 * 读取数据源
 * @author xiaxing
 *
 */
public class phoneSpout extends BaseRichSpout{

    //声明一个成员变量   将open里面的SpoutOutputCollector对象的发送方法  能传递给nextTuple()方法使用
    private SpoutOutputCollector collect ;
    //模拟数据
    String[] phones = {"iphone","xiaomi","sumsun","huawei","matepd","motopd","chuizi","oppopd","vivopd"};
    /**
     * 消息的处理方法   不断的往后续流程发送消息  调用一次发送一个消息脱了
     * 会连续不断的被worker进程的executor线程调用
     */
    public void nextTuple() {
        // TODO Auto-generated method stub
        int index = new Random().nextInt(phones.length);
        String phone = phones[index];
        //通过emit方法将phone发送出去
        collect.emit(new Values(phone));
        System.out.println("phone-name: "+phone);
        Utils.sleep(1000);
    }


    /**
     * open方法是组件的初始化方法。类似于MapReduce里面的mapper里面的setup方法
     */
    public void open(Map config, TopologyContext context, SpoutOutputCollector collect) {
        // TODO Auto-generated method stub
        this.collect = collect;
    }

    /**
     * 声明定义本组件发出的tuple的schema
     * 即发送的几个字段 每段字段的名称
     */
    public void declareOutputFields(OutputFieldsDeclarer declare) {
        // TODO Auto-generated method stub
        declare.declare(new Fields("phone-name"));
    }
}
