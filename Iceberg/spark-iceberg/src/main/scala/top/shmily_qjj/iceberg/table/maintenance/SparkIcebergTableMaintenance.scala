package top.shmily_qjj.iceberg.table.maintenance

import com.google.common.collect.ImmutableMap
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.iceberg.{ManifestFile, Table}
import org.apache.spark.sql.SparkSession
import org.apache.iceberg.catalog.TableIdentifier
import org.apache.iceberg.hadoop.HadoopCatalog
import org.apache.iceberg.hive.HiveCatalog
import org.apache.iceberg.spark.actions.SparkActions
import com.typesafe.scalalogging.Logger

/**
 * Iceberg Table Maintenance Using Spark
 * References: https://iceberg.apache.org/docs/latest/maintenance/
 */
object SparkIcebergTableMaintenance {
  private[this] val logger = Logger(this.getClass)

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder.master("local").appName("MaintenanceIcebergTables").getOrCreate()

    // 初始化hdfs conf
    val conf = new Configuration
    val hadoopResources = "/etc/hadoop-conf/core-site.xml,/etc/hadoop-conf/hdfs-site.xml"
    for (sourceFile <- hadoopResources.split(",")) {
      conf.addResource(new Path(sourceFile))
    }

    // kerberos认证
    setKerberos(conf, "/etc/krb5.conf", "/opt/keytabs/hdfs.keytab")

    val catalogType = "hive"
    // 获取表对象
    val table = catalogType match {
      case "hive" => getTable(conf, "http://shmily:9083", "hdfs://shmily:8020/user/hive/warehouse", "iceberg_db", "hive_iceberg_partitioned_table")
      case "hadoop" | "location_based_table" => getTable(conf, "hdfs://shmily:8020/user/iceberg/warehouse", "iceberg_db", "hadoop_iceberg_partitioned_table")
      case _ => throw new NotImplementedError(s"Catalog type ${catalogType} is not supported.")
    }


    // 合并数据文件到目标大小
    compactDataFiles(spark, table, 128L)

    // 清理过期快照
    expireSnapshots(spark, table, 20L, 5)

    // 重写Manifest
    rewriteManifests(spark, table, 10L)

    // 清理孤立文件
    removeOrphanFiles(spark, table)
  }

  def setKerberos(conf: Configuration, krb5Path: String,keytabPath: String) : Unit = {
    System.setProperty("java.security.krb5.conf", krb5Path)
    conf.setBoolean("hadoop.security.authentication", true)
    conf.set("hadoop.security.authentication", "Kerberos")
    import org.apache.hadoop.security.UserGroupInformation
    UserGroupInformation.setConfiguration(conf)
    UserGroupInformation.loginUserFromKeytab("hdfs", keytabPath)
  }

  // 获取HadoopCatalogTable对象
  def getTable(conf: Configuration, warehousePath: String, dbName: String, tableName: String): Table = {
    val catalog = new HadoopCatalog(conf, warehousePath)
    catalog.loadTable(TableIdentifier.of(dbName, tableName))
  }

  // 获取HiveCatalogTable对象
  def getTable(conf: Configuration, metastoreUris: String, warehousePath: String, dbName: String, tableName: String): Table = {
    val catalog = new HiveCatalog()
    catalog.setConf(conf)
    catalog.initialize("HiveCatalog", ImmutableMap.of("uri", metastoreUris, "warehouse", warehousePath, "clients", "5"))
    catalog.loadTable(TableIdentifier.of(dbName, tableName))
  }

  /**
   * 清理过期快照 避免快照过多
   * @param spark sparkSession
   * @param snapRetainMinutes 快照保留时间
   * @param snapRetainLastMinNum 快照保留数量
   */
  def expireSnapshots(spark: SparkSession, table: Table, snapRetainMinutes: Long, snapRetainLastMinNum: Int): Unit = {
    logger.info("Start ExpireSnapshots job for table " + table.name())
    val tsToExpire = System.currentTimeMillis() - (1000 * 60 * snapRetainMinutes)  //设置清理时间
    val result = SparkActions.get(spark).expireSnapshots(table).expireOlderThan(tsToExpire).retainLast(snapRetainLastMinNum).execute()
    logger.info(s"ExpireSnapshots Deleted Data Files Count:${result.deletedDataFilesCount()}")
    logger.info(s"ExpireSnapshots Deleted ManifestFiles Count:${result.deletedManifestsCount()}")
    logger.info(s"ExpireSnapshots Deleted ManifestLists Count:${result.deletedManifestListsCount()}")
    logger.info(s"ExpireSnapshots Deleted EqualityDeleteFiles Count:${result.deletedEqualityDeleteFilesCount()}")
    logger.info(s"ExpireSnapshots Deleted PositionDeleteFiles Count:${result.deletedPositionDeleteFilesCount()}")
  }

  /**
   * 清理孤立文件
   * In Spark and other distributed processing engines, task or job failures can leave files that are not referenced by table metadata,
   * and in some cases normal snapshot expiration may not be able to determine a file is no longer needed and delete it.
   * @param spark sparkSession
   */
  def removeOrphanFiles(spark: SparkSession, table: Table): Unit = {
    logger.info("Start RemoveOrphanFiles job for table " + table.name())
    val result = SparkActions.get(spark).deleteOrphanFiles(table).execute()
    logger.info("Removed orphan files: " + List(result.orphanFileLocations()).mkString(","))
  }

  /**
   * 并行合并数据文件 合并小文件
   * @param spark sparkSession
   * @param targetFileSizeMb 合并后的目标文件大小
   * Iceberg tracks each data file in a table. More data files leads to more metadata stored in manifest files,
   * and small data files causes an unnecessary amount of metadata and less efficient queries from file open costs.
   */
  def compactDataFiles(spark: SparkSession, table: Table, targetFileSizeMb: Long): Unit = {
    logger.info("Start CompactDataFiles job for table " + table.name())
    val result = SparkActions
      .get(spark)
      .rewriteDataFiles(table)
//      .filter(Expressions.equal("date", "2020-08-18"))
      .option("target-file-size-bytes", (targetFileSizeMb * 1024 * 1024).toString)
      .option("rewrite-all", "true")  // 避免清理得不干净 如果不加该参数 清理后还会有部分小文件
      .execute()
    logger.info(s"CompactDataFiles count: ${result.rewrittenDataFilesCount()}")
    if(result.rewrittenDataFilesCount() != 0){
      var totalRewrittenFileCnt = 0
      var totalAddedFileCnt = 0
      result.rewriteResults().forEach(r => {
        totalRewrittenFileCnt += r.rewrittenDataFilesCount()
        totalAddedFileCnt += r.addedDataFilesCount()
      })
      logger.info(s"CompactDataFiles RewrittenDataFilesCount: $totalAddedFileCnt AddedDataFilesCount: $totalAddedFileCnt}")
    }
  }

  /**
   * 优化表效率 重写Manifest清单文件
   * @param spark
   * @param rewriteThresholdMb
   * Iceberg uses metadata in its manifest list and manifest files speed up query planning and to prune unnecessary data files. The metadata tree functions as an index over a table’s data.
   * Manifests in the metadata tree are automatically compacted in the order they are added, which makes queries faster when the write pattern aligns with read filters. For example, writing hourly-partitioned data as it arrives is aligned with time range query filters.
   * When a table’s write pattern doesn’t align with the query pattern, metadata can be rewritten to re-group data files into manifests using rewriteManifests or the rewriteManifests action (for parallel rewrites using Spark).
   * This example rewrites small manifests and groups data files by the first partition field.
   */
  def rewriteManifests(spark: SparkSession, table:Table,  rewriteThresholdMb: Long): Unit = {
    logger.info("Start RewriteManifests job for table " + table.name())
    val rewritePredicate: java.util.function.Predicate[ManifestFile] = ((file: ManifestFile) => file.length() < rewriteThresholdMb * 1024 * 1024)
    val result = SparkActions
      .get(spark)
      .rewriteManifests(table)
      .rewriteIf(rewritePredicate) // 10 MB
      .execute()
    result.rewrittenManifests().forEach(m => logger.info(s"Rewritten manifest file: ${m.path()}"))
    result.addedManifests().forEach(m => logger.info(s"Added manifest file: ${m.path()}"))

  }


}
