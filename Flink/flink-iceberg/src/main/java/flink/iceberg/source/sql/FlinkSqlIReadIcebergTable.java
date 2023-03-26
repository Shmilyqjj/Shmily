package flink.iceberg.source.sql;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/10/6 18:16
 * @Site: shmily-qjj.top
 * 运行环境变量HADOOP_CONF_DIR=E:\CDH-Conf\
 */
public class FlinkSqlIReadIcebergTable {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        env.enableCheckpointing(5000L);

        // 配置table.dynamic-table-options.enabl=true 使sql可以使用Hint
        Configuration configuration = tEnv.getConfig().getConfiguration();
        configuration.setBoolean("table.dynamic-table-options.enable", true);

        tEnv.executeSql("CREATE CATALOG hadoop_iceberg_catalog WITH (\n" +
                "  'type'='iceberg',\n" +
                "  'catalog-type'='hadoop',\n" +
                "  'warehouse'='hdfs://nameservice/user/iceberg/warehouse',\n" +
                "  'property-version'='1'\n" +
                ")");

        tEnv.useCatalog("hadoop_iceberg_catalog");
        tEnv.useDatabase("iceberg_db");

        // 批量读取Iceberg表
        System.out.println("开始批量读取Iceberg表");
        TableResult tableResult = tEnv.executeSql("select * from flink_sql_hadoop_iceberg_table");
        tableResult.print();

        // 实时读取Iceberg表
        System.out.println("开始实时读取Iceberg表");
        TableResult tableStreamResult = tEnv.executeSql("select * from flink_sql_hadoop_iceberg_table /*+ OPTIONS('streaming'='true', 'monitor-interval'='2s')*/");  // OPTIONS可以指定snapshot 'start-snapshot-id'='3821550127947089987'
        tableStreamResult.print();

    }
}
