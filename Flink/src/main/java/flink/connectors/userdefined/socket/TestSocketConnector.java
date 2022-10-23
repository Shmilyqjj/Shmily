package flink.connectors.userdefined.socket;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/10/22 11:27
 * @Site: shmily-qjj.top
 */
public class TestSocketConnector {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        env.enableCheckpointing(5000L);

        // nc -lk 8888
        // > 1,qjj
        // > 2,abc
        tEnv.executeSql("create table socket_source(\n" +
                "  `id` string,\n" +
                "  `name` string\n" +
                ") with(\n" +
                "  'connector' = 'socket',\n" +
                "  'host' = 'cdh101',\n" +
                "  'port' = '8888',\n" +
                "  'format' = 'csv'\n" +
                ")");

        // nc -lk 9999
        tEnv.executeSql("create table socket_sink(id string, name string) \n" +
                "with(\n" +
                "  'connector' = 'socket',\n" +
                "  'host' = 'cdh101',\n" +
                "  'port' = '9999',\n" +
                "  'format' = 'csv'\n" +
                ")");

        tEnv.executeSql("insert into socket_sink select id,name from socket_source");


        //        tEnv.executeSql("CREATE TABLE print_table (\n" +
//                "  id STRING,\n" +
//                "  name STRING\n" +
//                "  ) WITH (\n" +
//                "  'connector' = 'print'\n" +
//                ")");
//        tEnv.executeSql("insert into print_table select id,name from socket_source");

    }
}
