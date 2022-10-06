package flink.study.streaming.source.iceberg;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.iceberg.flink.TableLoader;
import org.apache.iceberg.flink.source.FlinkSource;

/**
 * @author 佳境Shmily
 * @Description: Flink datastream api read iceberg table
 * @CreateTime: 2022/10/6 13:14
 * @Site: shmily-qjj.top
 */
public class HadoopCatalogIcebergTableSource {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // HadoopConf参数
        Configuration conf = new Configuration();
        String hadoopResources = "E:\\CDH-Conf\\core-site.xml,E:\\CDH-Conf\\hdfs-site.xml";
        for (String sourceFile : hadoopResources.split(",")) {
            conf.addResource(new Path(sourceFile));
        }

        // 加载Iceberg表
        String tableLocation = "/user/iceberg/warehouse/iceberg_db/flink_hadoop_iceberg_table";
        TableLoader tableLoader = TableLoader.fromHadoopTable(tableLocation, conf);

        // 读取数据
        boolean streamMode = true;  // 默认不加.streaming时是批读取  指定.streaming(true) 是流式读取
        DataStream<RowData> data = FlinkSource.forRowData().env(env)
                .tableLoader(tableLoader)
                .streaming(streamMode)
//                .startSnapshotId(4255413326651972910L)  // 可以指定基于某个快照ID后的增量数据读取
                .build();
        data.map(new MapFunction<RowData, String>() {
            @Override
            public String map(RowData rowData) throws Exception {
                int id = rowData.getInt(0);
                StringData name = rowData.getString(1);
                return "id="+ id + " name=" + name.toString();
            }
        }).print();

        env.execute("FlinkReadHadoopCatalogIcebergTable");
    }
}
