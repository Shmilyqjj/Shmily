package qjj.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * 创建topology 交给storm去处理任务
 */
public class APP {
    public static void main(String[] args){
        Config config = new Config();
        config.setDebug(true);//设置调试模式

        TopologyBuilder builder = new TopologyBuilder();//构建器
        builder.setSpout("call-log-reader-spout",new FakeCallReaderSpout());//设置Spout，把对象给它  第一个参数id是用来被其他组件引用的

        //设置bolts   shuffleGrouping分组，是指数据源到这个bolt的分组 即spout->bolt的分组
        builder.setBolt("call-log-creator-bolt",new CallLogCreatorBolt()).shuffleGrouping("call-log-reader-spout");

        //设置第二个bolt   fieldsGrouping按字段的分组，字段值是call对象-》设置分组策略
        builder.setBolt("call-log-counter-bolt",new CallLogCounterBolt()).fieldsGrouping("call-log-creator-bolt",new Fields("call"));

//        LocalCluster cluster = new LocalCluster();
//        cluster.submitTopology("LogAnalyserStorm",config,builder.createTopology());//交给本地Storm运行


//
//        代码提交到storm集群上运行
        try {
            StormSubmitter.submitTopology("LogAnalyserStorm", new Config(), builder.createTopology());
        } catch (AlreadyAliveException e) {
            e.printStackTrace();
        } catch (InvalidTopologyException e) {
            e.printStackTrace();
        } catch (AuthorizationException e) {
            e.printStackTrace();
        }
        //停止这个拓扑

    }
}
