package com.bigdata;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * 接收上一级bolt在phone-name添加格式化日期后缀
 * @author xiaxing
 *
 */
public class SuffixBolt extends BaseRichBolt{

    private OutputCollector collector;
    //创建一个时间对象
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //声明一个成员变量  调用emit方法
    public void execute(Tuple tuple) {
        // TODO Auto-generated method stub
        String upperName = tuple.getStringByField("upper-phone-name");
        //业务处理， 把upperName 加上日期后缀
        String result = upperName + "is "+df.format(System.currentTimeMillis());
        collector.emit(new Values(result));
    }

    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        // TODO Auto-generated method stub
        this.collector = collector;

    }
    //声明
    public void declareOutputFields(OutputFieldsDeclarer declare) {
        // TODO Auto-generated method stub
        declare.declare(new Fields("final"));
    }

}
