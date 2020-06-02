package qjj.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * Bolt
 * 转接头
 * 呼叫日志的Bolt
 * 实现IRichBolt接口(或者继承BaseRichBolt类)
 */
public class CallLogCreatorBolt extends BaseRichBolt {

    private OutputCollector collector;
    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) { //生命周期函数
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        String from = tuple.getString(0); //接收主叫
        String to = tuple.getString(1);   //接收被叫
        Integer duration = tuple.getInteger(2); //接收通话时长
        collector.emit(new Values(from+"-"+to,duration));//输出
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("call","duration"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
