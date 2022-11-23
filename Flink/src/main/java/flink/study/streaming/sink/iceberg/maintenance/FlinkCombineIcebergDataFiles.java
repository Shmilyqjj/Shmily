package flink.study.streaming.sink.iceberg.maintenance;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.hadoop.fs.Path;
import org.apache.iceberg.actions.RewriteDataFilesActionResult;
import org.apache.iceberg.catalog.Catalog;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.flink.actions.Actions;
import org.apache.hadoop.conf.Configuration;
import org.apache.iceberg.Table;
import org.apache.iceberg.hadoop.HadoopCatalog;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

/**
 * @author 佳境Shmily
 * @Description: 使用Flink合并Iceberg数据文件
 * @CreateTime: 2022/10/4 12:22
 * @Site: shmily-qjj.top
 */
public class FlinkCombineIcebergDataFiles {
    public static void main(String[] args) throws IOException {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // HadoopConf参数
        Configuration conf = new Configuration();
        setKerberos(conf, "/etc/krb5.conf", "/opt/keytabs/hdfs.keytab");

        String hadoopResources = "/etc/hadoop-conf/core-site.xml,/etc/hadoop-conf/hdfs-site.xml";
        for (String sourceFile : hadoopResources.split(",")) {
            conf.addResource(new Path(sourceFile));
        }

        // Hadoop Catalog Iceberg表信息
        String warehousePath = "hdfs://shmily:8020/user/iceberg/warehouse";
        String dbName = "iceberg_db";
        String tableName = "hadoop_iceberg_partitioned_table";

        Catalog catalog = new HadoopCatalog(conf, warehousePath);
        TableIdentifier name = TableIdentifier.of(dbName, tableName);
        Table table = catalog.loadTable(name);

        RewriteDataFilesActionResult result = Actions.forTable(env, table)
                .rewriteDataFiles()
//                .targetSizeInBytes(2048L)   // 测试用2kb  默认512M
                .execute();

        System.out.println("added data files:");
        result.addedDataFiles().forEach(System.out::println);
        System.out.println("deleted data files:");
        result.deletedDataFiles().forEach(System.out::println);
        // 注意：合并数据文件时 会按targetSize生成最新的文件，并在读取时读这个文件（会更新元数据到新的元数据文件），来解决小文件访问效率问题  但原文件在合并后不会被删除
    }


    private static void setKerberos(Configuration conf, String krb5Path, String keytabPath) throws IOException {
        System.setProperty("java.security.krb5.conf", krb5Path);
        conf.setBoolean("hadoop.security.authentication", true);
        conf.set("hadoop.security.authentication", "Kerberos");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab("hdfs", keytabPath);

    }
}
