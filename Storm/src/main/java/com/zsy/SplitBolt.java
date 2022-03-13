package com.zsy;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class SplitBolt extends BaseRichBolt {
    private OutputCollector collector;
    /**
     * 初始化方法  只是会被执行一次
     * @param stormConf
     * @param context
     * @param collector Bolt的发射器,指定下一个Bolt的地址
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    /**
     * 用于获取Spout发送过来的数据
     * 业务逻辑
     *  spout发送过来的数据是一行一行的line
     *  这里是需要line进行分割
     *
     * @param input
     */
    @Override
    public void execute(Tuple input) {
        String line = input.getStringByField("line");
        String[] words = line.split(",");

        for(String word : words){
//                这里把每一个单词发射出去
            this.collector.emit(new Values(word));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }
}
