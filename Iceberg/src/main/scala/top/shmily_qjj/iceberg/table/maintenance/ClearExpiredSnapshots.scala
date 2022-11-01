package top.shmily_qjj.iceberg.table.maintenance

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.iceberg.Table
import org.apache.iceberg.catalog.TableIdentifier
import org.apache.iceberg.hadoop.HadoopCatalog

object ClearExpiredSnapshots {
  def main(args: Array[String]): Unit = {

    val icebergWarehousePath = "hdfs://nameservice/user/iceberg/warehouse"
    val snapRetainMinutes = 100L  // 快照保留时间 min
    val snapRetainLastMinNum = 30 // 快照保留个数

    val expireOlderThanTimestamp = System.currentTimeMillis() - (1000 * 60 * snapRetainMinutes)
    val conf = new Configuration
    val hadoopResources = "E:\\CDH-Conf\\core-site.xml,E:\\CDH-Conf\\hdfs-site.xml"
    for (sourceFile <- hadoopResources.split(",")) {
      conf.addResource(new Path(sourceFile))
    }
    val hadoopCatalog = new HadoopCatalog(conf, icebergWarehousePath)

    val table: Table = hadoopCatalog.loadTable(TableIdentifier.of("iceberg_db", "flink_hadoop_iceberg_table"))

    // 使快照过期 清理快照文件
    table.expireSnapshots().expireOlderThan(expireOlderThanTimestamp).retainLast(snapRetainLastMinNum).commit()
  }

}
