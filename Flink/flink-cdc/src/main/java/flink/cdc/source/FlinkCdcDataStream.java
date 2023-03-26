package flink.cdc.source;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2023/3/26 16:14
 * @Site: shmily-qjj.top
 */
public class FlinkCdcDataStream {
    public static void main(String[] args) throws Exception {
        // 1.Flink ENV
        Configuration conf = new Configuration();

        // StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();  // 线上ENV
        conf.setInteger(RestOptions.PORT,8051);StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf); // 测试ENV
        env.setParallelism(4);
        env.enableCheckpointing(60000);
        env.getConfig().setAutoWatermarkInterval(200);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(100, 5000L));

        MySqlSource<String> ms = MySqlSource.<String>builder()
                .startupOptions(StartupOptions.latest())
                .hostname("node1.shmily-qjj.top")
                .port(3306)
                .username("root")
                .password("123456")
                .databaseList("test")
                .tableList("test.test_[0-9]{6}")
//                .deserializer(new StringDebeziumDeserializationSchema())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();

        env.fromSource(ms, WatermarkStrategy.noWatermarks(), "MySQL Source")
                // 设置 source 节点的并行度为 2
                .setParallelism(2)
                .print()
                // 设置 sink 节点并行度为 1
                .setParallelism(1);

        env.execute("MySQL CDC Datastream");

    }

}
