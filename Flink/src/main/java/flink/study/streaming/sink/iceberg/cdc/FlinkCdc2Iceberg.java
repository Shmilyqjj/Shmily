package flink.study.streaming.sink.iceberg.cdc;

import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.time.Duration;

/**
 * @author 佳境Shmily
 * @Description: Flink cdc to Iceberg
 * @CreateTime: 2022/12/19 11:49
 * @Site: shmily-qjj.top
 *
 * https://ververica.github.io/flink-cdc-connectors/master/content/quickstart/build-real-time-data-lake-tutorial.html
 */
public class FlinkCdc2Iceberg {
    public static void main(String[] args) throws IOException {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        String krb5File = "/etc/krb5.conf";
        String keyUser = "hive";
        String keyPath = "/opt/keytabs/hive.keytab";
        String keyPrincipal = "hive/shmily@SHMILY-QJJ.TOP";
        setHMSKerberos(krb5File, keyUser, keyPrincipal, keyPath);

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
        tableEnv.executeSql("DROP TABLE IF EXISTS cdc.flink_cdc_test");
        tableEnv.executeSql("CREATE TABLE cdc.flink_cdc_test(\n" +
                "    database_name STRING METADATA VIRTUAL,\n" +
                "    table_name STRING METADATA VIRTUAL,\n" +
                "    id INT PRIMARY KEY,\n" +
                "    name STRING,\n" +
                "    age INT,\n" +
                "    create_time TIMESTAMP(3)\n" +
                "  ) WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'localhost',\n" +
                "    'port' = '3306',\n" +
                "    'scan.startup.mode' = 'initial',\n" +
                "    'server-time-zone' = 'UTC',\n" +   // 时区要与mysql server端一致  否则无法运行
                "    'username' = 'root',\n" +
                "    'password' = 'root',\n" +
                "    'database-name' = 'test_[0-9]+',\n" +
                "    'table-name' = 'flink_cdc_[0-9]+'\n" +
                ")");

//        // print结果
//        tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS default_catalog.print");
//        tableEnv.executeSql("CREATE TABLE print.print_table (\n" +
//                "  database_name STRING,\n" +
//                "  table_name STRING,\n" +
//                "  id INT,\n" +
//                "  name STRING,\n" +
//                "  age INT,\n" +
//                "  create_time TIMESTAMP(3),\n" +
//                "  create_time_h STRING\n" +
//                ") WITH (\n" +
//                "  'connector' = 'print'\n" +
//                ")");
//        tableEnv.executeSql("insert into print.print_table select database_name,table_name,id,name,age,create_time,FROM_UNIXTIME(UNIX_TIMESTAMP(CAST(create_time AS STRING)), 'yyyy-MM-dd HH:mm:ss') from cdc.flink_cdc_test");

        // 写入Iceberg
        // 1.创建Iceberg Catalog
        tableEnv.executeSql("CREATE CATALOG hive_iceberg_catalog WITH (\n" +
                "  'type'='iceberg',\n" +
                "  'catalog-type'='hive',\n" +
                "  'uri'='thrift://shmily:9083',\n" +
                "  'clients'='5',\n" +
                "  'property-version'='2',\n" +
                "  'warehouse'='hdfs://shmily:8020/user/iceberg/warehouse',\n" +
                "  'hive-conf-dir'='/etc/hive-conf'\n" +
                ")");

        // 2.创建Iceberg表
        // 注意 如果希望iceberg与mysql数据保持一致,则主键必须设置正确
        // 如果主键设置错误或未设置主键 则update\delete操作也会被当成insert操作,导致数据重复
        // 如果write.distribution-mode=hash 则分区字段也要加到联合主键中
        tableEnv.executeSql("CREATE TABLE if not exists `hive_iceberg_catalog`.`iceberg_db`.`flink_cdc_iceberg` (\n" +
                "  mysql_db STRING,\n" +
                "  mysql_table STRING,\n" +
                "  id INT,\n" +
                "  name STRING,\n" +
                "  age INT,\n" +
                "  create_time TIMESTAMP(3),\n" +
                "  ds STRING,\n" +
                "  PRIMARY KEY (mysql_db, mysql_table, `id`) NOT ENFORCED\n" +
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
        tableEnv.executeSql("insert into hive_iceberg_catalog.iceberg_db.flink_cdc_iceberg select database_name,table_name,id,name,age,create_time,FROM_UNIXTIME(UNIX_TIMESTAMP(CAST(create_time AS STRING)), 'yyyyMMddHH') from cdc.flink_cdc_test");

    }

    public static void FlinkCDC2Iceberg(StreamExecutionEnvironment env){

    }

    private static void setHMSKerberos(String krb5File, String keyUser, String principal, String keytabPath) throws IOException {
        System.setProperty("java.security.krb5.conf", krb5File); //可以直接在启动脚本里面设置 export xxx=xxx
        System.setProperty("krb.principal", keyUser);
        Configuration hadoopConf = new Configuration();
        hadoopConf.set("hadoop.security.authentication", "kerberos");
        hadoopConf.set("kerberos.principal", principal);
        UserGroupInformation.setConfiguration(hadoopConf);
        UserGroupInformation.loginUserFromKeytab(keyUser, keytabPath);
    }



}
