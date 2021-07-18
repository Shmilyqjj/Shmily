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

public class Spout_2 extends BaseRichSpout {
    private SpoutOutputCollector spoutOutputCollector;
    private int i = 0;
    String[] money ={"1999","2999","3999","999","99","9","1666","2666","3666","6","7","11"};
    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.spoutOutputCollector =spoutOutputCollector;
    }

    @Override
    public void nextTuple() {
        Random random =new Random();
        int flag =random.nextInt(money.length);
        String price = money[flag];
        if ((i = Integer.valueOf(price))<2000){
            String out = price;
            this.spoutOutputCollector.emit("LowerStream",new Values(out));
        }else {
            String out2 = price;
            this.spoutOutputCollector.emit("HigherStream",new Values(out2));
        }

        Utils.sleep(3000);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream("HigherStream",true,new Fields("money"));
        outputFieldsDeclarer.declareStream("LowerStream",true,new Fields("money"));
    }
}
