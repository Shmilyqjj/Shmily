package flink.study.streaming.sink;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.iceberg.flink.IcebergTableSink;
import org.apache.iceberg.flink.TableLoader;
import org.apache.iceberg.flink.sink.FlinkSink;

import java.util.Properties;

/**
 * @author 佳境Shmily
 * @Description: Kafka etl to Iceberg Table
 * @CreateTime: 2022/8/13 15:22
 * @Site: shmily-qjj.top
 *
 * // kafka-topics --create --zookeeper cdh101:2181 --replication-factor 2 --partitions 1 --topic t_kafka2iceberg
 * // kafka-topics --zookeeper cdh101:2181 --list
 * // kafka-console-producer --broker-list cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092 --topic t_kafka2iceberg
 */
public class KafkaSinkIceberg {
    public static void main(String[] args) throws Exception {
        // 0.Stream Env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 1.设置checkpoint ,Flink向Iceberg中写入数据时当checkpoint发生后，才会commit数据。
        env.enableCheckpointing(5000);


        // 2.连接kafka数据源
        String topicName = "t_kafka2iceberg";
        Properties kafkaConnProperties = new Properties();
        kafkaConnProperties.setProperty("bootstrap.servers", "cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092");
        kafkaConnProperties.setProperty("group.id", "flink-source-cg");
        kafkaConnProperties.setProperty("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConnProperties.setProperty("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConnProperties.setProperty("auto.offset.reset", "latest");
        DataStreamSource<String> kafkaStreamSource = env.addSource(new FlinkKafkaConsumer<String>(topicName, new SimpleStringSchema(), kafkaConnProperties));

        // 3.根据清洗逻辑，将DataStreamSource<String>包装成RowData对象
        SingleOutputStreamOperator<RowData> dataStream = kafkaStreamSource.map(new MapFunction<String, RowData>() {
            @Override
            public RowData map(String s) throws Exception {
                String[] split = s.split(",");
                GenericRowData row = new GenericRowData(2);
                row.setField(0, Integer.valueOf(split[0]));
                row.setField(1, StringData.fromString(split[1]));
                return row;
            }
        });



        // Hadoop参数
        Configuration conf = new Configuration();
        String hadoopResources = "E:\\CDH-Conf\\core-site.xml,E:\\CDH-Conf\\hdfs-site.xml";
        for (String sourceFile : hadoopResources.split(",")) {
            conf.addResource(new Path(sourceFile));
        }

        // 加载Iceberg表
        String tablePath = "hdfs://nameservice/user/hive/warehouse/iceberg_db.db/hive_iceberg_table_flink";
        TableLoader tableLoader = TableLoader.fromHadoopTable(tablePath, conf);
        TableLoader.CatalogTableLoader catalogTableLoader = new TableLoader.CatalogTableLoader()

        FlinkSink.forRowData(dataStream)
                .tableLoader(tableLoader)
                .build();

        env.execute("Test Iceberg DataStream");
    }
}
