package qjj.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * 第二个Bolt
 * 转接头
 * 呼叫日志的计数器
 * 数据流从第一个Bolt中获取
 */
public class CallLogCounterBolt extends BaseRichBolt {
    private OutputCollector collector;//收集器
    Map<String,Integer> counterMap;//集合
    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        String call = tuple.getString(0); //得到主叫打给被叫的信息
        Integer duration = tuple.getInteger(1); //提取元组的数据
        if(!counterMap.containsKey(call)){
            counterMap.put(call,1);
        }else{
            Integer c = counterMap.get(call)+1;
            counterMap.put(call,c);
        }
        collector.ack(tuple);//确认这个tuple被处理/被消费了 - 通知zk
    }

    @Override
    public void cleanup() {
        for(Map.Entry<String,Integer> entry : counterMap.entrySet()){
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
