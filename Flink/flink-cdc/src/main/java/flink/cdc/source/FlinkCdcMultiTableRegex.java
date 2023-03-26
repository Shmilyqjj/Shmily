package flink.cdc.source;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

/**
 * @Description: Flink CDC多库表 同步
 * @author 佳境Shmily
 * @CreateTime: 2023/3/26 13:04
 * @Site: shmily-qjj.top
 *
 * MySQL table DDL:
 * CREATE TABLE test.`test_202303` (
 *   `id` int(11) AUTO_INCREMENT,
 *   `username` varchar(255) NOT NULL,
 *   `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '默认取当前时间',
 *   PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 */
public class FlinkCdcMultiTableRegex {
    public static void main(String[] args) {
        // 1.Flink ENV
        Configuration configuration = new Configuration();

        // StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();  // 线上ENV
        configuration.setInteger(RestOptions.PORT,8050);StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration); // 测试ENV
        env.setParallelism(4);
        env.enableCheckpointing(10000);
        env.getConfig().setAutoWatermarkInterval(200);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(100, 5000L));

        // 2.Flink Table ENV
        EnvironmentSettings tEnvConf = EnvironmentSettings.newInstance().inStreamingMode().build();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, tEnvConf);
        tEnv.getConfig().getConfiguration().set(ExecutionCheckpointingOptions.CHECKPOINTING_MODE, CheckpointingMode.EXACTLY_ONCE);
        tEnv.getConfig().getConfiguration().set(ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL, Duration.ofMinutes(1));

        cdcMultiTableToPrint(tEnv);


    }


    public static void cdcMultiTableToPrint(StreamTableEnvironment tEnv){
        tEnv.executeSql("CREATE DATABASE IF NOT EXISTS default_catalog.cdc");



        tEnv.executeSql("CREATE TABLE IF NOT EXISTS cdc.flink_cdc_test_regex(\n" +
                "    database_name STRING METADATA FROM 'database_name' VIRTUAL,\n" +
                "    table_name STRING METADATA  FROM 'table_name' VIRTUAL,\n" +
                "    op_ts TIMESTAMP_LTZ(3) METADATA FROM 'op_ts' VIRTUAL,\n" +
                "    id INT,\n" +
                "    username STRING,\n" +
                "    update_time TIMESTAMP(3),\n" +
                "    PRIMARY KEY(id) NOT ENFORCED\n" +
                ")WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'node1.shmily-qjj.top',\n" +
                "    'port' = '3306',\n" +
                "    'username' = 'root',\n" +
                "    'password' = '123456',\n" +
                "    'database-name' = 'test',\n" +   //库名 test
                "    'table-name' = 'test_[0-9]{6}',\n" +    // 表名 test_yyyyMM
                "    'scan.startup.mode' = 'latest-offset',\n" +
                "    'connect.max-retries' = '60',\n" +
                "    'connection.pool.size' = '12',\n" +
                "    'jdbc.properties.useSSL' = 'false'\n" +   // jdbc.properties.* 是jdbc连接?后的参数
                ")");

        tEnv.executeSql("CREATE TABLE IF NOT EXISTS print_table(\n" +
                "    database_name STRING,\n" +
                "    table_name STRING,\n" +
                "    op_ts TIMESTAMP_LTZ(3),\n" +
                "    id INT,\n" +
                "    username STRING,\n" +
                "    update_time TIMESTAMP(3)\n" +
                ")WITH (\n" +
                "    'connector' = 'print'\n" +
                ")");

        tEnv.executeSql("insert into print_table select * from cdc.flink_cdc_test_regex");
    }
}
