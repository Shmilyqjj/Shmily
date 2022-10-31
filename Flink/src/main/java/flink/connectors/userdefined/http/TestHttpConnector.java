package flink.connectors.userdefined.http;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class TestHttpConnector {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        env.enableCheckpointing(5000L);

        // Manjaro: nc -l -p 8888
        // CentOS : nc -lk 8888
        // Data {"name": "qjj", "age": "24"}    1
        tEnv.executeSql("create table socket_source(\n" +
                "  `args` string,\n" +
                "  `d` string\n" +
                ") with(\n" +
                "  'connector' = 'socket',\n" +
                "  'host' = 'localhost',\n" +
                "  'port' = '8888',\n" +
                "  'format' = 'csv',\n" +
                "  'csv.field-delimiter' = '\\\t'\n" +
                ")");

//        tEnv.executeSql("CREATE TABLE print_table (\n" +
//                "  args STRING\n" +
//                "  ) WITH (\n" +
//                "  'connector' = 'print'\n" +
//                ")");
//        tEnv.executeSql("insert into print_table select args from socket_source");


        tEnv.executeSql("CREATE TABLE http_sink (\n" +
                "  args STRING\n" +
                "  ) WITH (\n" +
                "  'connector' = 'http',\n" +
                "  'url' = 'http://localhost:8080/hello',\n" +
                "  'retry.interval.ms' = '1000',\n" +
                "  'retry.max.num' = '5',\n" +
                "  'retry.strategy' = 'keep',\n" +
                "  'method' = 'POST'\n" +
                ")");
        tEnv.executeSql("insert into http_sink select args from socket_source");

//        tEnv.executeSql("CREATE TABLE http_sink (\n" +
//                "  args STRING\n" +
//                "  ) WITH (\n" +
//                "  'connector' = 'http',\n" +
//                "  'url' = 'http://localhost:8080/hello?name=qjj&&age=24',\n" +
//                "  'retry.interval.ms' = '1000',\n" +
//                "  'retry.max.num' = '5',\n" +
//                "  'retry.strategy' = 'ignore',\n" +
//                "  'method' = 'post'\n" +
//                ")");
//        tEnv.executeSql("insert into http_sink select args from socket_source");

    }

}
