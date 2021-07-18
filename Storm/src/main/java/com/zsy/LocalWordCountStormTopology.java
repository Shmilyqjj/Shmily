package com.zsy;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.shade.org.apache.commons.io.FileUtils;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LocalWordCountStormTopology {
    /**
     * 读取数据并发送到Bolt上去
     */
    public static class DataSourceSpout extends BaseRichSpout {
        //定义一个发射器
        private SpoutOutputCollector collector;

        /**
         * 初始化方法 只是会被调用一次
         * @param conf  配置参数
         * @param context  上下文
         * @param collector  数据发射器
         */
        @Override
        public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
            //对上面定义的的发射器进行赋初值
            this.collector = collector;
        }

        /**
         * 用于数据的产生
         * 业务:
         * 1.读取指定目录的文件夹下的数据
         * 2.把每一行数据发射出去
         */
        @Override
        public void nextTuple() {
//            获取所有文件,这里指定文件的后缀
            Collection<File> files = FileUtils.listFiles(new File("E:\\StormTest"),new String[]{"txt"},true);
//            循环遍历每一个文件 ==>  由于这里指定的是文件夹下面的目录 所以就是需要进行循环遍历
            for( File file : files){
                try {
//                    获取每一个文件的每一行
                    List<String> lines =  FileUtils.readLines(file);
                    for(String line : lines){
//                        把每一行数据发射出去
                        this.collector.emit(new Values(line));
                    }
                    //TODO 数据处理完毕之后 改名  否则的话 会一直执行的
                    FileUtils.moveFile(file,new File(file.getAbsolutePath()+System.currentTimeMillis()));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * 声明输出字段名称
         * @param declarer
         */
        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("line"));
        }
    }
    /**
     * 对Spout发送过来的数据进行分割
     */

    /**
     * 主函数
     * @param args
     */
    public static void main(String[] args) {
//            使用TopologyBuilder根据Spout和Bolt构建Topology
        TopologyBuilder builder = new TopologyBuilder();
//            设置Bolt和Spout  设置Spout和Bolt的关联关系
        builder.setSpout("DataSourceSpout",new DataSourceSpout());
        builder.setBolt("SplitBolt",new SplitBolt()).shuffleGrouping("DataSourceSpout");
        builder.setBolt("CountBolt",new CountBolt()).shuffleGrouping("SplitBolt");
//            创建一个本地的集群
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("LocalWordCountStormTopology",new Config(),builder.createTopology());
    }
}

