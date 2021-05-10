package com.MultiSpoutBolt;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class Bolt_a extends BaseRichBolt {
    private OutputCollector outputCollector;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String value = tuple.getString(0);
        String sourceStreamId = tuple.getSourceStreamId();
        if(sourceStreamId.equals("HigherStream")){
            value = "高价手机";
            String out = value+" "+"from_a";
            outputCollector.emit(new Values(out));
            return ;
        }

        if(sourceStreamId.equals("Spout1")){
            String out2 = value+" "+"from_a";
            outputCollector.emit(new Values(out2));
            return ;
        }
//            outputCollector.emit(new Values(value));


    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("phone"));
    }
}
