package flink.iceberg.source;

import com.google.common.collect.ImmutableMap;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.iceberg.FileFormat;
import org.apache.iceberg.TableProperties;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.flink.CatalogLoader;
import org.apache.iceberg.flink.TableLoader;
import org.apache.iceberg.flink.source.FlinkSource;

import java.util.Map;

/**
 * @author 佳境Shmily
 * @Description: Flink datastream api read iceberg table
 * @CreateTime: 2022/10/6 13:14
 * @Site: shmily-qjj.top
 */
public class HiveCatalogIcebergTableSource {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // HiveConf参数
        Configuration conf = new Configuration();
        String hadoopResources = "E:\\CDH-Conf\\core-site.xml,E:\\CDH-Conf\\hdfs-site.xml,E:\\CDH-Conf\\hive-site.xml";
        for (String sourceFile : hadoopResources.split(",")) {
            conf.addResource(new Path(sourceFile));
        }

        // 加载Iceberg表
        String db = "iceberg_db";
        String tableName = "flink_hive_iceberg_tb";
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
        TableLoader tableLoader = TableLoader.fromCatalog(CatalogLoader.hive("hive", conf, tblprops), TableIdentifier.of(db, tableName));

        // 读取数据
        boolean streamMode = false;  // 默认不加.streaming时是批读取  指定.streaming(true) 是流式读取
        DataStream<RowData> data = FlinkSource.forRowData().env(env)
                .tableLoader(tableLoader)
                .streaming(streamMode)
//                .startSnapshotId(4255413326651972910L)  // 可以指定基于某个快照ID后的增量数据读取
                .build();
        data.map(new MapFunction<RowData, String>() {
            @Override
            public String map(RowData rowData) throws Exception {
                long id = rowData.getLong(0);
                StringData name = rowData.getString(1);
                StringData dt = rowData.getString(2);
                return "id="+ id + " name=" + name.toString() + " dt=" + dt.toString();
            }
        }).print();

        env.execute("FlinkReadHiveCatalogIcebergTable");
    }
}
