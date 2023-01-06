package flink.study.cdc.mysql;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.time.Duration;

/**
 * Flink CDC consume mysql binlog
 * Mysql开启binlog
 * /etc/my.cnf 在mysqld下添加
 #开启binlog日志
 log-bin=/var/lib/mysql/mysql-bin
 #设置binlog清理时间
 expire_logs_days = 7
 #binlog每个日志文件大小
 max_binlog_size = 100m
 #binlog缓存大小
 binlog_cache_size = 4m
 #最大binlog缓存大小
 max_binlog_cache_size = 512m
 #配置serverid
 server-id=1
 * 重启mysqld
 * 检查是否开启成功 SHOW VARIABLES LIKE 'log_bin'; show global variables like "binlog%";
 *
 * References:
 * https://github.com/ververica/flink-cdc-connectors
 * https://ververica.github.io/flink-cdc-connectors/master/
 * https://ververica.github.io/flink-cdc-connectors/master/content/connectors/mysql-cdc.html
 *
 * MySQL操作:
 * create table test.flink_cdc_test (id int,name varchar(255),age int,create_time timestamp);
 * desc test.flink_cdc_test;
 * insert into test.flink_cdc_test(id,name,age) values (0,'init',0);
 * select * from test.flink_cdc_test;
 * insert into test.flink_cdc_test(id,name,age) values (1,'qjj',24);
 * insert into test.flink_cdc_test(id,name,age) values (2,'abc',24);
 * insert into test.flink_cdc_test(id,name,age) values (3,'def',24);
 * insert into test.flink_cdc_test(id,name,age) values (4,'ghi',24);
 * insert into test.flink_cdc_test(id,name,age) values (5,'jkl',24);
 * delete from test.flink_cdc_test where id = 4;
 */
public class MysqlFlinkCDC {
    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 本地debug用env  webui：localhost:8050
        org.apache.flink.configuration.Configuration flinkConf = new org.apache.flink.configuration.Configuration();
        flinkConf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER, true);
        flinkConf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(flinkConf);
        // 设置并发
        env.setParallelism(4);
        //设置checkpoint 30s
        env.enableCheckpointing(10000);
        env.getConfig().setAutoWatermarkInterval(200);
        // 指定重启策略
//        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 10000L));  // 固定延迟重启策略 设置3次，那么在第4次异常时，程序才会退出。重试间隔10s
        env.setRestartStrategy(RestartStrategies.failureRateRestart(9, Time.minutes(10), Time.seconds(5)));  //失败率重启策略 10分钟内，最大失败9次（第9次错误发生时，程序退出）,而且每次失败重启间隔为5秒

        // 设置Flink SQL环境
        EnvironmentSettings tableEnvSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        // 创建table Env
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, tableEnvSettings);
        tableEnv.getConfig().getConfiguration().set(ExecutionCheckpointingOptions.CHECKPOINTING_MODE, CheckpointingMode.EXACTLY_ONCE);
        // 设置checkpoint间隔
        tableEnv.getConfig().getConfiguration().set(ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL, Duration.ofMinutes(1));

        // 1.FlinkSQL CDC Demo
//        flinkSqlCDC(tableEnv);
//        flinkSqlCDC(tableEnv, env);

        // 2.Flink DataStream CDC Demo
        flinkDataStreamCDC(env);

    }

    /**
     * Problems:
     * 1. Caused by: org.apache.flink.table.api.ValidationException: The MySQL server has a timezone offset (0 seconds ahead of UTC) which does not match the configured timezone Asia/Shanghai. Specify the right server-time-zone to avoid inconsistencies for time-related fields.
     *   解决: CDC连接器的server-time-zone参数设置与MySQL服务端相同时区
     *
     * @param tableEnv flink table env
     */
    public static void flinkSqlCDC(StreamTableEnvironment tableEnv){
        // 创建mysql cdc 数据表
        tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS default_catalog.cdc");
        tableEnv.executeSql("DROP TABLE IF EXISTS cdc.flink_cdc_test");
        tableEnv.executeSql("CREATE TABLE cdc.flink_cdc_test(\n" +
                "    db_name STRING METADATA FROM 'database_name' VIRTUAL,\n" +
                "    table_name STRING METADATA  FROM 'table_name' VIRTUAL,\n" +
                "    operation_ts TIMESTAMP_LTZ(3) METADATA FROM 'op_ts' VIRTUAL,\n" +
                "    id INT PRIMARY KEY,\n" +
                "    name STRING,\n" +
                "    age INT,\n" +
                "    create_time TIMESTAMP(3)\n" +
                "  ) WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'localhost',\n" +
                "    'port' = '3306',\n" +
                "    'scan.startup.mode' = 'initial',\n" +
                "    'server-time-zone' = 'UTC',\n" +   // Asia/Shanghai
                "    'username' = 'root',\n" +
                "    'password' = 'root',\n" +
                "    'database-name' = 'test',\n" +
                "    'table-name' = 'flink_cdc_test'\n" +
                ")");

        // 输出到printTable
        tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS default_catalog.print");
        tableEnv.executeSql("CREATE TABLE print.print_table (\n" +
                "  db_name STRING,\n" +
                "  table_name STRING,\n" +
                "  operation_ts TIMESTAMP_LTZ(3),\n" +
                "  id INT,\n" +
                "  name STRING,\n" +
                "  age INT,\n" +
                "  create_time TIMESTAMP(3),\n" +
                "  create_time_h STRING\n" +
                ") WITH (\n" +
                "  'connector' = 'print'\n" +
                ")");

        tableEnv.executeSql("insert into print.print_table select db_name,table_name,operation_ts,id,name,age,create_time,FROM_UNIXTIME(UNIX_TIMESTAMP(CAST(create_time AS STRING)), 'yyyy-MM-dd HH:mm:ss') from cdc.flink_cdc_test");

    }


    /**
     * Table转DataStream消费
     * @param tableEnv StreamTableEnvironment
     * @param env StreamExecutionEnvironment
     */
    public static void flinkSqlCDC(StreamTableEnvironment tableEnv, StreamExecutionEnvironment env) throws Exception {
        // 创建mysql cdc 数据表
        tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS default_catalog.cdc");
        tableEnv.executeSql("DROP TABLE IF EXISTS cdc.flink_cdc_test");
        tableEnv.executeSql("CREATE TABLE cdc.flink_cdc_test(\n" +
                "    db_name STRING METADATA FROM 'database_name' VIRTUAL,\n" +
                "    table_name STRING METADATA  FROM 'table_name' VIRTUAL,\n" +
                "    operation_ts TIMESTAMP_LTZ(3) METADATA FROM 'op_ts' VIRTUAL,\n" +
                "    id INT PRIMARY KEY,\n" +
                "    name STRING,\n" +
                "    age INT,\n" +
                "    create_time TIMESTAMP(3)\n" +
                "  ) WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'hostname' = 'localhost',\n" +
                "    'port' = '3306',\n" +
                "    'scan.startup.mode' = 'initial',\n" +
                "    'server-time-zone' = 'UTC',\n" +   // Asia/Shanghai
                "    'username' = 'root',\n" +
                "    'password' = 'root',\n" +
                "    'database-name' = 'test',\n" +
                "    'table-name' = 'flink_cdc_test'\n" +
                ")");

        // table转DataStream消费
        Table table = tableEnv.sqlQuery("select * from cdc.flink_cdc_test");
        DataStream<Tuple2<Boolean, Row>> retractStream = tableEnv.toRetractStream(table, Row.class);
        retractStream.print();
        env.execute("Flink SQL MySql CDC");

    }

    public static void flinkDataStreamCDC(StreamExecutionEnvironment env) throws Exception {
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(3306)
                .username("root")
                .password("root")
                .serverTimeZone("UTC")
                .databaseList("test")  // 可指定多个db
                .tableList("test.flink_cdc_test", "test.flink_cdc_test1")   // 可指定多个table
                .deserializer(new StringDebeziumDeserializationSchema())
                .startupOptions(StartupOptions.initial())
                .build();

        env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Binlog")
                // set 4 parallel source tasks
                .setParallelism(4)
                // use parallelism 1 for sink to keep message ordering
                .print().setParallelism(1);
        env.execute("DataStream MySql CDC");
    }


}
