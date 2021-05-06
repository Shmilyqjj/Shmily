package WorldCountStorm;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Map;
import java.util.Random;

public class WordCountSpout extends BaseRichSpout {
    private SpoutOutputCollector collector;
    //模拟产生一些数据
    private String[] data = {"Beijing","China","capital"};

    /**
     * open方法的作用主要是将collector进行初始化
     * collector的作用：将采集到的数据发送给下一个组件
     */

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector collector) {
        this.collector=collector;
    }


    public void nextTuple() {
        Utils.sleep(3000);
        int random = (new Random()).nextInt(3);
        String value = data[random];
        System.out.println("产生的随机值是"+value);
        //发送给下一个组件
        collector.emit(new Values(value));
    }


    //申明发送给下一个组件的tuple的schema（结构）
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("sentence"));
    }
}

