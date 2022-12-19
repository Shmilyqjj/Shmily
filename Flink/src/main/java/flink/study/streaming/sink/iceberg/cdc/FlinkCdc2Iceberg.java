package flink.study.streaming.sink.iceberg.cdc;

import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

/**
 * @author 佳境Shmily
 * @Description: Flink cdc to Iceberg
 * @CreateTime: 2022/12/19 11:49
 * @Site: shmily-qjj.top
 */
public class FlinkCdc2Iceberg {
    public static void main(String[] args) {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 本地debug用env  webui：localhost:8050
        org.apache.flink.configuration.Configuration flinkConf = new org.apache.flink.configuration.Configuration();
        flinkConf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER, true);
        flinkConf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(flinkConf);
        // 设置并发
        env.setParallelism(4);
        //设置checkpoint 10s
        env.enableCheckpointing(10000);
        env.getConfig().setAutoWatermarkInterval(200);
        // 指定重启策略
//        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 10000L));

        // 设置Flink SQL环境
        EnvironmentSettings tableEnvSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        // 创建table Env
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, tableEnvSettings);
        tableEnv.getConfig().getConfiguration().set(ExecutionCheckpointingOptions.CHECKPOINTING_MODE, CheckpointingMode.EXACTLY_ONCE);
        // 设置checkpoint间隔
        tableEnv.getConfig().getConfiguration().set(ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL, Duration.ofMinutes(1));

        FlinkCDCSQL2Iceberg(tableEnv);

    }


    public static void FlinkCDCSQL2Iceberg(StreamTableEnvironment tableEnv){
        tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS default_catalog.cdc");
        tableEnv.executeSql("DROP TABLE IF EXISTS cdc.test_cdc");
        tableEnv.executeSql("CREATE TABLE cdc.test_cdc(\n" +
                "    id INT PRIMARY KEY,\n" +
                "    name STRING,\n" +
                "    age INT,\n" +
                "    create_time TIMESTAMP(3)\n" +
                "  ) WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'localhost',\n" +
                "    'port' = '3306',\n" +
                "    'scan.startup.mode' = 'initial',\n" +
                "    'server-time-zone' = 'Asia/Shanghai',\n" +   // UTC
                "    'username' = 'root',\n" +
                "    'password' = '123456',\n" +
                "    'database-name' = 'test',\n" +
                "    'table-name' = 'test_cdc'\n" +
                ")");

//        // print结果
//        tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS default_catalog.print");
//        tableEnv.executeSql("CREATE TABLE print.print_table (\n" +
//                "  id INT,\n" +
//                "  name STRING,\n" +
//                "  age INT,\n" +
//                "  create_time TIMESTAMP(3),\n" +
//                "  create_time_h STRING\n" +
//                ") WITH (\n" +
//                "  'connector' = 'print'\n" +
//                ")");
//        tableEnv.executeSql("insert into print.print_table select id,name,age,create_time,FROM_UNIXTIME(UNIX_TIMESTAMP(CAST(create_time AS STRING)), 'yyyy-MM-dd HH:mm:ss') from cdc.test_cdc");

        // 写入Iceberg
        // 1.创建Iceberg Catalog
        tableEnv.executeSql("CREATE CATALOG hive_iceberg_catalog WITH (\n" +
                "  'type'='iceberg',\n" +
                "  'catalog-type'='hive',\n" +
                "  'uri'='thrift://cdh101:9083,thrift://cdh103:9083',\n" +
                "  'clients'='5',\n" +
                "  'property-version'='2',\n" +
                "  'warehouse'='hdfs://nameservice/user/iceberg/warehouse',\n" +
                "  'hive-conf-dir'='/E/CDH-Conf'\n" +
                ")");

        // 2.创建Iceberg表
        tableEnv.executeSql("CREATE TABLE if not exists `hive_iceberg_catalog`.`iceberg_db`.`flink_cdc_iceberg` (\n" +
                "  id INT,\n" +
                "  name STRING,\n" +
                "  age INT,\n" +
                "  create_time TIMESTAMP(3),\n" +
                "  ds STRING\n" +
                ") PARTITIONED BY (ds)\n" +
                "WITH('type'='ICEBERG',\n" +
                "'engine.hive.enabled'='true',\n" +
                "'read.split.target-size'='1073741824',\n" +
                "'write.target-file-size-bytes'='134217728',\n" +
                "'write.format.default'='parquet',\n" +
                "'write.parquet.compression-codec'='zstd',\n" +
                "'write.parquet.compression-level'='10',\n" +
                "'write.metadata.delete-after-commit.enabled'='true',\n" +
                "'write.metadata.previous-versions-max'='5',  \n" +
                "'format-version'='2',  \n" +
                "'write.distribution-mode'='none')");
        // 3.写入Iceberg
        tableEnv.executeSql("insert into hive_iceberg_catalog.iceberg_db.flink_cdc_iceberg select id,name,age,create_time,FROM_UNIXTIME(UNIX_TIMESTAMP(CAST(create_time AS STRING)), 'yyyyMMddHH') from cdc.test_cdc");
    }

    public static void FlinkCDC2Iceberg(StreamExecutionEnvironment env){

    }



}
