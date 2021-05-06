package WorldCountStorm;


import org.apache.storm.shade.com.google.common.collect.Maps;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class WordCountTotalBolt extends BaseRichBolt{

    private OutputCollector collector;
    Map<String,Integer> result= Maps.newHashMap();
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        String word = tuple.getStringByField("word");
        Integer count = tuple.getIntegerByField("count");

        if (result.get(word) == null){
            result.put(word,count);
        }else {
            result.put(word,count + result.get(word));
        }
        result.entrySet().forEach(enty-> System.out.println("单词："+enty.getKey()+" " + "数量："+ enty.getValue()));

        collector.emit(new Values(word,result.get(word)));

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word","total"));
    }
}
