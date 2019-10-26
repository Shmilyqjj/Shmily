package com.bigdata;

import org.apache.storm.hdfs.bolt.HdfsBolt;
import org.apache.storm.hdfs.bolt.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.bolt.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

public class Submitter {

    public static void main(String[] args) {
        /**
         * 定义出HDFS的Bolt
         */
        //输出字段分隔符
        RecordFormat format = new DelimitedRecordFormat().withFieldDelimiter("|");
        //每10个tuple同步到HDFS上一次
        SyncPolicy syncPolicy = new CountSyncPolicy(10);
        //每个写出文件的大小为5M
        FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(5.0f,FileSizeRotationPolicy.Units.MB);
        //获取输出目录  从cli命令行上获取 此处使用本地测试，因此手动指定目录，集群将此换成args[0]即可。
        FileNameFormat fileNameFormat = new DefaultFileNameFormat().withPath("/StormResults/phoneStorm");

        //执行HDFS地址，实测8020端口连接失败，9000端口可连接。可在hadoop中进行端口配置
        HdfsBolt hdfsBolt = new HdfsBolt()
                .withFsUrl("hdfs://192.168.1.101:9000")
                .withFileNameFormat(fileNameFormat)
                .withRecordFormat(format)
                .withRotationPolicy(rotationPolicy)
                .withSyncPolicy(syncPolicy);

        //构建一个topo构建器
        TopologyBuilder builder = new TopologyBuilder();

        //指定topo所有的spout组件类
        //参数1  spout的id   参数2  spout的实例对象 (并且可以设置并行度)
        builder.setSpout("phone-spout", new phoneSpout());

        //指定topo所用的第一个bolt组件,同时指定本bolt的消息流是从哪个组件流过来的
        builder.setBolt("upper-bolt", new UpperBolt()).shuffleGrouping("phone-spout");
        builder.setBolt("suffix-bolt", new SuffixBolt()).shuffleGrouping("upper-bolt");
        builder.setBolt("hdfs-bolt", hdfsBolt).shuffleGrouping("suffix-bolt");

        //使用builder来生成一个Topology的对象
        StormTopology phoneTopo = builder.createTopology();
        if(args.length > 0) {
            try {
                StormSubmitter.submitTopology("phoneTopology", new Config(), phoneTopo);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else {
            //本地测试
            System.out.println("不能在集群上运行，开始运行本地测试");
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("phone1-topo", new Config(), phoneTopo);
        }

    }
}

