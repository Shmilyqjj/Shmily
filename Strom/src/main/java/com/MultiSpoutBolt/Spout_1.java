package com.MultiSpoutBolt;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Map;
import java.util.Random;

public class Spout_1 extends BaseRichSpout {
    private SpoutOutputCollector spoutOutputCollector;
    String[] words = {"华为","1001","oppo","2311","锤子","4511","二加","6788","四家"};
    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.spoutOutputCollector = spoutOutputCollector;
    }

    @Override
    public void nextTuple() {
        Random random = new Random();
        int index =random.nextInt(words.length);
        String phone = words[index];
        try {
            int i = Integer.valueOf(phone);
        }catch (NumberFormatException e){
            phone = "错误数据";
        }
        String out = phone;
        spoutOutputCollector.emit("Spout1",new Values(out));
        Utils.sleep(3000);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
//        outputFieldsDeclarer.declare(new Fields("phone"));
        outputFieldsDeclarer.declareStream("Spout1",true,new Fields("phone"));
    }
}
