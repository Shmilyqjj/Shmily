package WorldCountStorm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class WordCountSplitBolt extends BaseRichBolt {

        //bolt组件的收集器 用于将数据发送给下一个bolt
        private OutputCollector collector;


        //初始化
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
            this.collector = collector;
        }


        public void execute(Tuple tuple) {
            //处理上一级发来的数据
            String value = tuple.getStringByField("sentence");
            String[] data= value.split(" ");
            //输出
            for (String word : data){
                collector.emit(new Values(word,1));
            }
        }


        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            //申明发送给下一个组件的tuple schema结构
            declarer.declare(new Fields("word","count"));
        }
    }

