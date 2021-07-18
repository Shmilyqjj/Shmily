package qjj.storm;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 创建一个spout
 * 需要实现IRichSpout接口(或者继承BaseRichSpout类)
 * 水龙头，不断产生数据，这里限制1000
 */
public class FakeCallReaderSpout extends BaseRichSpout {
    private  SpoutOutputCollector collector; //收集器
    private boolean completed = false;       //标记位-是否完成
    private TopologyContext context;         //Topology的上下文-环境信息
    private Random randomGenerator = new Random();
    private Integer idx = 0;

    @Override
    public void open(Map map, TopologyContext context, SpoutOutputCollector collector) { //initiallize初始化，是生命周期方法open
        this.context = context;
        this.collector = collector;
    }

    @Override
    public void nextTuple() {//下一个元组 - 该方法非阻塞
        if(this.idx <= 1000){ //idx是阈值，nextTuple是一个循环过程，一个一个tuple流入，idx限制了循环次数，相当于是计数器
            List<String> mobileNumbers = new ArrayList<String>();
            mobileNumbers.add("1234123401");
            mobileNumbers.add("1234123402");
            mobileNumbers.add("1234123403");
            mobileNumbers.add("1234123404");
            Integer localIdx = 0;
            while(localIdx++ < 100 && this.idx++ <1000){
                String fromMobileNumber = mobileNumbers.get(randomGenerator.nextInt(4)); //主叫//从这个集合中取出一个元素-随机取
                String toMobileNumber = mobileNumbers.get(randomGenerator.nextInt(4));//被叫
                while(fromMobileNumber == toMobileNumber){ //主叫不能给自己打电话
                    toMobileNumber = mobileNumbers.get(randomGenerator.nextInt(4));//所以从新取
                }
                Integer duration = randomGenerator.nextInt(60);//duration通话时长/持续时间0-59之间随机
                this.collector.emit(new Values(fromMobileNumber,toMobileNumber,duration));//Values对象参数是多个对象，形成集合--输出通话记录
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) { //声明输出字段，起了字段名称from to duration
        declarer.declare(new Fields("from","to","duration"));
    }

    @Override
    public void close() {
        //生命周期函数  结束生命周期
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void ack(Object o) {

    }

    @Override
    public void fail(Object o) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
