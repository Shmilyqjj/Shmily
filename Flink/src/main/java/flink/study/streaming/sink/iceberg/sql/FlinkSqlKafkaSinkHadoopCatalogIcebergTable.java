package flink.study.streaming.sink.iceberg.sql;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author 佳境Shmily
 * @Description: 使用FlinkSQL写HadoopCatalog Iceberg表
 * @CreateTime: 2022/10/6 17:14
 * @Site: shmily-qjj.top
 * 运行环境变量HADOOP_CONF_DIR=E:\CDH-Conf\
 * Hive映射表
 create external table iceberg_db.flink_sql_hadoop_iceberg_table (
   id BIGINT,
   name STRING,
   age INT,
   dt STRING
 )
 STORED BY 'org.apache.iceberg.mr.hive.HiveIcebergStorageHandler'
 LOCATION 'hdfs://nameservice/user/iceberg/warehouse/iceberg_db/flink_sql_hadoop_iceberg_table'
 tblproperties ('iceberg.catalog'='location_based_table');
 */


public class FlinkSqlKafkaSinkHadoopCatalogIcebergTable {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        env.enableCheckpointing(5000L);
        // 创建Kafka数据源表
        tEnv.executeSql("CREATE TABLE t_kafka_source (\n" +
                "    id BIGINT,\n" +
                "    name STRING,\n" +
                "    age INT,\n" +
                "    dt STRING\n" +
                ") WITH (\n" +
                "    'connector' = 'kafka',\n" +
                "    'topic' = 't_kafka2iceberg',\n" +
                "    'scan.startup.mode' = 'latest-offset',\n" +
                "    'properties.bootstrap.servers' = 'cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092',\n" +
                "    'properties.group.id' = 'flink-source-cg',\n" +
                "    'format' = 'csv'\n" +
                ")");

        // 创建Iceberg Catalog
        tEnv.executeSql("CREATE CATALOG hadoop_iceberg_catalog WITH (\n" +
                "  'type'='iceberg',\n" +
                "  'catalog-type'='hadoop',\n" +
                "  'warehouse'='hdfs://nameservice/user/iceberg/warehouse',\n" +
                "  'property-version'='1'\n" +
                ")");

        // 创建Iceberg表
        tEnv.executeSql("CREATE TABLE if not exists `hadoop_iceberg_catalog`.`iceberg_db`.`flink_sql_hadoop_iceberg_table` (\n" +
                "   id BIGINT,\n" +
                "   name STRING,\n" +
                "   age INT,\n" +
                "   dt STRING\n" +
                ") PARTITIONED BY (dt)\n" +
                "WITH('type'='ICEBERG',\n" +
                "'engine.hive.enabled'='true',\n" +
                "'read.split.target-size'='1073741824',\n" +
                "'write.target-file-size-bytes'='134217728',\n" +
                "'write.format.default'='parquet',\n" +
                "'write.metadata.delete-after-commit.enabled'='true',\n" +
                "'write.metadata.previous-versions-max'='9',  \n" +
                "'write.distribution-mode'='hash')");

        // 流式读取kafka表并写入Iceberg表
        tEnv.executeSql("insert into hadoop_iceberg_catalog.iceberg_db.flink_sql_hadoop_iceberg_table select id,name,age,dt from t_kafka_source");


    }

}
