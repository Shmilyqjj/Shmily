package flink.study.streaming.sink;

import com.google.common.collect.ImmutableMap;
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
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.fs.Path;
import org.apache.iceberg.*;
import org.apache.iceberg.catalog.Catalog;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.flink.CatalogLoader;
import org.apache.iceberg.flink.IcebergTableSink;
import org.apache.iceberg.flink.TableLoader;
import org.apache.iceberg.flink.sink.FlinkSink;
import org.apache.iceberg.hadoop.HadoopCatalog;
import org.apache.iceberg.hive.HiveCatalog;
import org.apache.iceberg.types.Types;

import java.util.Map;
import java.util.Properties;

import static org.apache.flink.streaming.api.CheckpointingMode.EXACTLY_ONCE;

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
        // 一定设置Checkpoint 因为Flink依赖checkpoint、savepoint提交数据 没开启就查不到数据
        // env.enableCheckpointing(3000L, EXACTLY_ONCE);
        env.enableCheckpointing(5000);

        // 2.连接kafka数据源
        String topicName = "t_kafka2iceberg";
        Properties kafkaConnProperties = new Properties();
        kafkaConnProperties.setProperty("bootstrap.servers", "cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092");
        kafkaConnProperties.setProperty("group.id", "flink-source-cg");
        kafkaConnProperties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConnProperties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConnProperties.setProperty("auto.offset.reset", "latest");
        DataStreamSource<String> kafkaStreamSource = env.addSource(new FlinkKafkaConsumer<String>(topicName, new SimpleStringSchema(), kafkaConnProperties));

        // 3.根据清洗逻辑，将DataStreamSource<String>包装成RowData对象
        SingleOutputStreamOperator<RowData> dataStream = kafkaStreamSource.map((MapFunction<String, RowData>) s -> {
            String[] split = s.split(",");
            GenericRowData row = new GenericRowData(2);
            row.setField(0, Integer.valueOf(split[0]));
            row.setField(1, StringData.fromString(split[1]));
            return row;
        });

        // HadoopConf参数
        Configuration conf = new Configuration();
        String hadoopResources = "E:\\CDH-Conf\\core-site.xml,E:\\CDH-Conf\\hdfs-site.xml";
        for (String sourceFile : hadoopResources.split(",")) {
            conf.addResource(new Path(sourceFile));
        }

        // append datastream to hadoop catalog iceberg table
        String warehousePath = "hdfs://nameservice/user/hive/warehouse";
//        sinkToIceberg(conf, warehousePath, dataStream);

        // HiveConf参数
        String hiveResources = "E:\\CDH-Conf\\core-site.xml,E:\\CDH-Conf\\hdfs-site.xml,E:\\CDH-Conf\\hive-site.xml";
        Configuration hiveConf = new HiveConf();
        for (String sourceFile : hiveResources.split(",")) {
            hiveConf.addResource(new Path(sourceFile));
        }

        dataStream.print();
        // append datastream to hive catalog iceberg table
        sinkToIceberg(hiveConf, dataStream);

        env.execute("Test Iceberg DataStream");
    }

    public static void sinkToIceberg(Configuration hadoopConf, String warehousePath, SingleOutputStreamOperator<RowData> dataStream){
        // 加载HadoopCatalog的Iceberg表
        Catalog catalog = new HadoopCatalog(hadoopConf, warehousePath);

        // 设置要写入的库名表名
        String dbName = "default";
        String tableName = "flink_sql_iceberg_table";


        //配置iceberg 库名和表名
        TableIdentifier name = TableIdentifier.of(dbName, tableName);

        //创建Iceberg表Schema
        Schema schema = new Schema(
                Types.NestedField.required(1, "id", Types.IntegerType.get()),
                Types.NestedField.required(2, "name", Types.StringType.get()));

        //如果有分区指定对应分区，这里“loc”列为分区列，可以指定unpartitioned 方法不设置表分区
        PartitionSpec spec = PartitionSpec.unpartitioned();
//         PartitionSpec spec = PartitionSpec.builderFor(schema).identity("name").build();

        //指定Iceberg表数据格式化为Parquet存储
        Map<String, String> props = ImmutableMap.<String,String>builder()
                .put(TableProperties.DEFAULT_FILE_FORMAT, FileFormat.PARQUET.name())
                .put(TableProperties.PARQUET_COMPRESSION_DEFAULT, "snappy")
                .build();

        Table table = null;
        // 通过catalog判断表是否存在，不存在就创建，存在就加载
        if (!catalog.tableExists(name)) {
            System.out.println("table not exists,now create.");
            table = catalog.createTable(name, schema, spec, props);
        }else {
            System.out.println("table exists");
            table = catalog.loadTable(name);
        }

        TableLoader tableLoader = TableLoader.fromHadoopTable(warehousePath + "/" + dbName + "/" + tableName, hadoopConf);

        //通过DataStream Api 向Iceberg中写入数据
        FlinkSink.forRowData(dataStream)
                //这个 .table 也可以不写，指定tableLoader 对应的路径就可以。
                .table(table)
                .tableLoader(tableLoader)
                // 默认为false,追加数据。如果设置为true 就是覆盖数据
                .overwrite(false)
                .build();

    }



    public static void sinkToIceberg(Configuration hiveConf, SingleOutputStreamOperator<RowData> dataStream){
        // 加载HiveCatalog的Iceberg表
        Catalog catalog = new HiveCatalog(hiveConf);

        //配置iceberg 库名和表名
        TableIdentifier name = TableIdentifier.of("iceberg_db", "hive_iceberg_table_flink");

        //创建Iceberg表Schema
        Schema schema = new Schema(
                Types.NestedField.required(1, "id", Types.IntegerType.get()),
                Types.NestedField.required(2, "name", Types.StringType.get()));

        //如果有分区指定对应分区，这里“loc”列为分区列，可以指定unpartitioned 方法不设置表分区
        PartitionSpec spec = PartitionSpec.unpartitioned();
        // PartitionSpec spec = PartitionSpec.builderFor(schema).identity("loc").build();

        //指定Iceberg表数据格式化为Parquet存储
        Map<String, String> props = ImmutableMap.<String,String>builder()
                .put(TableProperties.DEFAULT_FILE_FORMAT, FileFormat.PARQUET.name())
                .put(TableProperties.PARQUET_COMPRESSION_DEFAULT, "snappy")
                .build();

        Table table = null;
        // 通过catalog判断表是否存在，不存在就创建，存在就加载
        if (!catalog.tableExists(name)) {
            System.out.println("table not exists,now create.");
            table = catalog.createTable(name, schema, spec, props);
        }else {
            System.out.println("table exists");
            table = catalog.loadTable(name);
        }

        TableLoader tableLoader = TableLoader.fromCatalog(CatalogLoader.hive("hive", hiveConf, props), name);

        //5.通过DataStream Api 向Iceberg中写入数据
        FlinkSink.forRowData(dataStream)
                //这个 .table 也可以不写，指定tableLoader 对应的路径就可以。
                .table(table)
                .tableLoader(tableLoader)
                //默认为false,追加数据。如果设置为true 就是覆盖数据
                .overwrite(false)
                .build();

    }


}
