package WorldCountStorm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class WordCountTopology {
    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();

        //1 指定任务的spout组件
        builder.setSpout("1", new WordCountSpout());

        //2 指定任务的第一个bolt组件
        builder.setBolt("2",new WordCountSplitBolt()).shuffleGrouping("1");

        //3 指定任务的第二个bolt组件
        builder.setBolt("3",new WordCountTotalBolt()).fieldsGrouping("2",new Fields("word"));

        //创建任务
        StormTopology job = builder.createTopology();

        Config config = new Config();

        //运行任务有两种模式
        //1 本地模式   2 集群模式

//        //1、本地模式
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("MyWordCount",config,job);


//        2、集群模式：用于打包jar，并放到storm运行
//        StormSubmitter.submitTopology("WordCountTopology",config,job);
    }
}
