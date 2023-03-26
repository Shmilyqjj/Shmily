package flink.iceberg.sink;

import com.google.common.collect.ImmutableMap;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.iceberg.*;
import org.apache.iceberg.catalog.Catalog;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.flink.CatalogLoader;
import org.apache.iceberg.flink.TableLoader;
import org.apache.iceberg.flink.sink.FlinkSink;
import org.apache.iceberg.hadoop.HadoopCatalog;
import org.apache.iceberg.hive.HiveCatalog;
import org.apache.iceberg.types.Types;
import org.apache.flink.configuration.ConfigConstants;

import java.util.Map;
import java.util.Properties;

/**
 * @author 佳境Shmily
 * @Description:
 * @CreateTime: 2022/10/4 10:25
 * @Site: shmily-qjj.top
 * // kafka-topics --create --zookeeper cdh101:2181 --replication-factor 2 --partitions 1 --topic t_kafka2iceberg
 * // kafka-topics --zookeeper cdh101:2181 --list
 * // kafka-console-producer --broker-list cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092 --topic t_kafka2iceberg
 *
 * 建议先在hive建表再写入 (以保证presto和hive都能正常读写)
 CREATE TABLE iceberg_db.flink_hive_iceberg_tb (
   id BIGINT,
   name STRING
 ) partitioned by (dt string)
 STORED BY 'org.apache.iceberg.mr.hive.HiveIcebergStorageHandler'
 TBLPROPERTIES (
  'write.distribution-mode'='hash',
  'write.metadata.delete-after-commit.enabled'='true',
  'write.metadata.previous-versions-max'='10'
 );
 *
 * 如果先在flink中建表并写入，在presto读取正常，在hive无法直接读取，需要修改表属性，使用以下命令 (问题：如果实时程序在写入，会实时修改表的Storage Information和storage_handler导致无法读写)
 *  ALTER TABLE iceberg_db.flink_hive_iceberg_tb SET FILEFORMAT INPUTFORMAT "org.apache.iceberg.mr.hive.HiveIcebergInputFormat" OUTPUTFORMAT "org.apache.iceberg.mr.hive.HiveIcebergOutputFormat" SERDE "org.apache.iceberg.mr.hive.HiveIcebergSerDe";
 *  ALTER TABLE iceberg_db.flink_hive_iceberg_tb SET TBLPROPERTIES ('storage_handler'='org.apache.iceberg.mr.hive.HiveIcebergStorageHandler');
 *
 * 注意flink与iceberg表字段类型对应，否则无法写入表
 */

public class KafkaSinkHiveCatalogIcebergTable {
    public static void main(String[] args) throws Exception {
        // 0.Stream Env
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 本地debug用env  webui：localhost:8050
        org.apache.flink.configuration.Configuration flinkConf = new org.apache.flink.configuration.Configuration();
        flinkConf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(flinkConf);


        // 1.设置checkpoint ,Flink向Iceberg中写入数据时当checkpoint发生后，才会commit数据。
        // 一定设置Checkpoint 因为Flink依赖checkpoint、savepoint提交数据 没开启就查不到数据
        env.enableCheckpointing(3000L, CheckpointingMode.EXACTLY_ONCE);

        // 2.连接kafka数据源
        String topicName = "t_kafka2iceberg";
        String groupId = "flink-source-cg";
        String brokers = "cdh101:9092,cdh102:9092,cdh103:9092,cdh104:9092";
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(brokers)
                .setTopics(topicName)
                .setGroupId(groupId)
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(OffsetsInitializer.latest())
                .build();
        DataStreamSource<String> kafkaStreamSource = env.fromSource(source, WatermarkStrategy.noWatermarks(),"KafkaSource");

        // 3.根据清洗逻辑，将DataStreamSource<String>包装成RowData对象
        SingleOutputStreamOperator<RowData> dataStream = kafkaStreamSource.map((MapFunction<String, RowData>) s -> {
            String[] split = s.split(",");
            GenericRowData row = new GenericRowData(3);
            row.setField(0, Long.valueOf(split[0]));
            row.setField(1, StringData.fromString(split[1]));
            row.setField(2, StringData.fromString(split[2]));
            return row;
        });


        // 4.指定参数
        // HiveConf参数
        Configuration conf = new Configuration();
        String hadoopResources = "E:\\CDH-Conf\\core-site.xml,E:\\CDH-Conf\\hdfs-site.xml,E:\\CDH-Conf\\hive-site.xml";
        for (String sourceFile : hadoopResources.split(",")) {
            conf.addResource(new Path(sourceFile));
        }

        //表参数
        String metastoreUris = "thrift://cdh101:9083,thrift://cdh103:9083";
        String db = "iceberg_db";
        String tableName = "flink_hive_iceberg_tb";


        // 5.写入HiveCatalogIceberg表
        // 5.1 加载HiveCatalog
        HiveCatalog catalog = new HiveCatalog();
        catalog.setConf(conf);

        catalog.initialize("IcebergHiveCatalog", ImmutableMap.of("uri", metastoreUris, "clients", "5"));

        // 5.2 配置iceberg表的库名和表名
        TableIdentifier name = TableIdentifier.of(db, tableName);

        // 5.3 指定Iceberg表的Schema
        Schema schema = new Schema(
                Types.NestedField.required(1, "id", Types.IntegerType.get()),
                Types.NestedField.required(2, "name", Types.StringType.get()),
                Types.NestedField.required(3, "dt", Types.StringType.get()));

        // 5.4 如果有分区指定对应分区，这里“dt”列为分区列，可以指定unpartitioned 方法不设置表分区
         PartitionSpec spec = PartitionSpec.builderFor(schema).identity("dt").build();
//         PartitionSpec spec = PartitionSpec.unpartitioned();

        // 5.4 指定Iceberg表参数
        Map<String, String> tblprops = ImmutableMap.<String,String>builder()
                .put(TableProperties.DEFAULT_FILE_FORMAT, FileFormat.PARQUET.name())
                .put(TableProperties.PARQUET_COMPRESSION_DEFAULT, "snappy")
                .put(TableProperties.WRITE_TARGET_FILE_SIZE_BYTES, "134217728")
                .put(TableProperties.WRITE_DISTRIBUTION_MODE, "hash")
                .put(TableProperties.METADATA_DELETE_AFTER_COMMIT_ENABLED, "true") // 自动metadata清理
                .put(TableProperties.METADATA_PREVIOUS_VERSIONS_MAX, "10")  // 保留metadata.json的数量 建表时指定才生效 如果只是程序指定 不生效
                .put(TableProperties.MIN_SNAPSHOTS_TO_KEEP, "100")
                .put(TableProperties.MAX_SNAPSHOT_AGE_MS, "5184000000")
                .put(TableProperties.MAX_REF_AGE_MS, "5184000000")
                .build();

        // 5.5 通过catalog判断表是否存在，不存在就创建，存在就加载
        Table table;
        if (!catalog.tableExists(name)) {
            System.out.println("table not exists,now create.");
            table = catalog.createTable(name, schema, spec, tblprops);
            System.out.println("table created by flink.It can be read by presto but not support in hive.");
        }else {
            System.out.println("table already exists");
            table = catalog.loadTable(name);
        }

        TableLoader tableLoader = TableLoader.fromCatalog(CatalogLoader.hive("hive", conf, tblprops), name);
        System.out.println(tableLoader);

        //5.通过DataStream Api 向Iceberg中写入数据
        FlinkSink.forRowData(dataStream)
                //这个 .table 也可以不写，指定tableLoader 对应的路径就可以。
                .table(table)
                .tableLoader(tableLoader)
                //默认为false,追加数据。如果设置为true 就是覆盖数据
                .overwrite(false)
                .append();

        dataStream.print();
        env.execute("Kafka2HiveCatalogIcebergTable");

    }
}
