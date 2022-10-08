package top.shmily_qjj.iceberg.table.maintenance

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.iceberg.Table
import org.apache.iceberg.catalog.TableIdentifier
import org.apache.iceberg.hadoop.HadoopCatalog

object IcebergTableManager {
  def main(args: Array[String]): Unit = {

    // oss://xldw-cdh/data/iceberg/warehouse/iceberg_db/hadoop_iceberg_table_flink_sql

    //expire过期快照
    val icebergWarehousePath = "hdfs://nameservice/user/iceberg/warehouse"

    val snapRetainMinutes = 100L
    val expireOlderThanTimestamp = System.currentTimeMillis() - (1000 * 60 * snapRetainMinutes)

    val conf = new Configuration()
    conf.addResource(new Path("/etc/ecm/hadoop-conf/core-site.xml"))
    conf.addResource(new Path("/etc/ecm/hadoop-conf/hdfs-site.xml"))
    val hadoopCatalog = new HadoopCatalog(conf, icebergWarehousePath)


    val table: Table = hadoopCatalog.loadTable(TableIdentifier.of("iceberg_db", "hadoop_iceberg_table_flink_sql"))
    table.expireSnapshots().expireOlderThan(expireOlderThanTimestamp).retainLast(10).commit()
  }

}
