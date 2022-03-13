package com.MultiSpoutBolt;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

public class Topology_phone {
    public static void main(String[] args) throws InterruptedException, InvalidTopologyException, AuthorizationException, AlreadyAliveException
    {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("Spout_1",new Spout_1(),2);
        builder.setSpout("Spout_2",new Spout_2(),2);
        builder.setBolt("Bolt_a",new Bolt_a(),4).shuffleGrouping("Spout_1","Spout1").shuffleGrouping("Spout_2","HigherStream");
        builder.setBolt("Bolt_b",new Bolt_b(),4).shuffleGrouping("Bolt_a");
        builder.setBolt("Bolt_c",new Bolt_c(),-+4).shuffleGrouping("Spout_2","LowerStream");
        builder.setBolt("Bolt_d",new Bolt_d(),4).shuffleGrouping("Bolt_c","MinStream");
        builder.setBolt("Bolt_e",new Bolt_e(),4).shuffleGrouping("Bolt_c","MidStream");
        builder.setBolt("Bolt_finish",new Bolt_finish(),4).shuffleGrouping("Bolt_b").shuffleGrouping("Bolt_d").shuffleGrouping("Bolt_e");

        Config config = new Config();
        StormTopology job = builder.createTopology();
//        StormSubmitter.submitTopology("Topology_phone", config, builder.createTopology());
        LocalCluster localCluster = new LocalCluster();  //本地提交
        localCluster.submitTopology("Topology_phone",config,job);


//        cluster.submitTopology("Topology", config, job);

    }

}

