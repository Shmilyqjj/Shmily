package com.zsy;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 词频汇总的Bolt
 */
public  class CountBolt extends BaseRichBolt {
    /**
     * 由于这里是不需要向外部发射  所以就不需要定义Collector
     * @param stormConf
     * @param context
     * @param collector
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    }
    Map<String,Integer> map = new HashMap<String, Integer>();
    /**
     * 业务逻辑
     * 1.获取每一个单词
     * 2.对每一个单词进行汇总
     * 3.输出结果
     * @param input
     */
    @Override
    public void execute(Tuple input) {
//            获取每一个单词
        String word = input.getStringByField("word");
        Integer count =  map.get(word);
        if (count == null){
            count = 0;
        }
        count++;
//           对单词进行汇总
        map.put(word,count);
//           输出
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
        Set<Map.Entry<String,Integer>> entrySet = map.entrySet();
        for(Map.Entry<String,Integer> entry :entrySet){
            System.out.println(entry);
        }
    }
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}