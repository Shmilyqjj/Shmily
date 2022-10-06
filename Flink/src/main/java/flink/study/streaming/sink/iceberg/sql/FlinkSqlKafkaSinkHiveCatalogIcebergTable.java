package flink.study.streaming.sink.iceberg.sql;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author 佳境Shmily
 * @Description: 使用FlinkSQL写HiveCatalog Iceberg表
 * @CreateTime: 2022/10/6 17:50
 * @Site: shmily-qjj.top
 * 运行环境变量HADOOP_CONF_DIR=E:\CDH-Conf\
 * Hive先建表
  CREATE TABLE iceberg_db.flink_sql_hive_iceberg_tb (
    id BIGINT,
    name STRING
  ) partitioned by (dt string)
  STORED BY 'org.apache.iceberg.mr.hive.HiveIcebergStorageHandler'
  TBLPROPERTIES (
   'write.distribution-mode'='hash',
   'write.metadata.delete-after-commit.enabled'='true',
   'write.metadata.previous-versions-max'='10'
  );
 */


public class FlinkSqlKafkaSinkHiveCatalogIcebergTable {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        env.enableCheckpointing(5000L);

        // 创建Kafka数据源表
        tEnv.executeSql("CREATE TABLE t_kafka_source (\n" +
                "    id BIGINT,\n" +
                "    name STRING,\n" +
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
        tEnv.executeSql("CREATE CATALOG hive_iceberg_catalog WITH (\n" +
                "  'type'='iceberg',\n" +
                "  'catalog-type'='hive',\n" +
                "  'uri'='thrift://cdh101:9083,thrift://cdh103:9083',\n" +
                "  'clients'='5',\n" +
                "  'property-version'='1',\n" +
                "  'warehouse'='hdfs://nameservice/user/iceberg/warehouse'\n" +
                ")");

        // 创建Iceberg表
        tEnv.executeSql("CREATE TABLE if not exists `hive_iceberg_catalog`.`iceberg_db`.`flink_sql_hive_iceberg_tb` (\n" +
                "   id BIGINT,\n" +
                "   name STRING,\n" +
                "   dt STRING\n" +
                ") PARTITIONED BY (dt)\n" +
                "WITH('type'='ICEBERG',\n" +
                "'engine.hive.enabled'='true',\n" +
                "'read.split.target-size'='1073741824',\n" +
                "'write.target-file-size-bytes'='134217728',\n" +
                "'write.format.default'='parquet',\n" +
                "'write.metadata.delete-after-commit.enabled'='true',\n" +
                "'write.metadata.previous-versions-max'='10',  \n" +
                "'write.distribution-mode'='hash')");

        // 流式读取kafka表并写入Iceberg表
        tEnv.executeSql("insert into hive_iceberg_catalog.iceberg_db.flink_sql_hive_iceberg_tb select id,name,dt from t_kafka_source");

    }

}
