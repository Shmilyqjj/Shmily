package top.shmily_qjj.iceberg.demos

import com.google.common.collect.ImmutableMap
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.iceberg.{ManifestFile, Table}
import org.apache.iceberg.catalog.TableIdentifier
import org.apache.iceberg.hadoop.HadoopCatalog
import org.apache.iceberg.hive.HiveCatalog

/**
 * 使用Iceberg原生API 单线程执行Iceberg表快照过期操作
 */
object ClearExpiredSnapshots {
  def main(args: Array[String]): Unit = {
    val conf = new Configuration
    val hadoopResources = "/etc/hadoop-conf/core-site.xml,/etc/hadoop-conf/hdfs-site.xml"
    for (sourceFile <- hadoopResources.split(",")) {
      conf.addResource(new Path(sourceFile))
    }
    setKerberos(conf, "/etc/krb5.conf", "/opt/keytabs/hdfs.keytab")

    // Hadoop table
//    val hadoopCatalog = new HadoopCatalog(conf, "hdfs://shmily:8020/user/iceberg/warehouse/")
//    val table: Table = hadoopCatalog.loadTable(TableIdentifier.of("iceberg_db", "hadoop_iceberg_partitioned_table"))

    // Hive table
    val hiveCatalog = new HiveCatalog()
    hiveCatalog.setConf(conf)
    hiveCatalog.initialize("HiveCatalog", ImmutableMap.of("uri", "thrift://shmily:9083", "warehouse", "hdfs://shmily:8020/user/hive/warehouse", "clients", "5"))
    val table: Table = hiveCatalog.loadTable(TableIdentifier.of("iceberg_db", "hive_iceberg_partitioned_table"))


    // 使快照过期 清理快照文件
    val snapRetainMinutes = 100L // 快照保留时间 min
    val snapRetainLastMinNum = 2 // 快照保留个数
    val expireOlderThanTimestamp = System.currentTimeMillis() - (1000 * 60 * snapRetainMinutes)
    table.expireSnapshots().expireOlderThan(expireOlderThanTimestamp).retainLast(snapRetainLastMinNum).commit()

    // 优化表效率 重写Manifest清单文件
    val rewriteThresholdMb = 10  //10 MB
    val rewritePredicate: java.util.function.Predicate[ManifestFile] = (file: ManifestFile) => file.length() < rewriteThresholdMb * 1024 * 1024
    table.rewriteManifests().rewriteIf(rewritePredicate)

  }

  def setKerberos(conf: Configuration, krb5Path: String,keytabPath: String) : Unit = {
    System.setProperty("java.security.krb5.conf", krb5Path)
    conf.setBoolean("hadoop.security.authentication", true)
    conf.set("hadoop.security.authentication", "Kerberos")
    import org.apache.hadoop.security.UserGroupInformation
    UserGroupInformation.setConfiguration(conf)
    UserGroupInformation.loginUserFromKeytab("hdfs", keytabPath)

  }

}
