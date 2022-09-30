package top.shmily_qjj.iceberg.table.maintenance

import org.apache.iceberg.ManifestFile
import org.apache.spark.sql.SparkSession
import org.apache.iceberg.catalog.TableIdentifier
import org.apache.iceberg.expressions.{Expressions, Predicate}
import org.apache.iceberg.hive.HiveCatalog
import org.apache.iceberg.spark.actions.SparkActions

/**
 * Iceberg Table Maintenance Using Spark
 * References: https://iceberg.apache.org/docs/latest/maintenance/
 */
object SparkIcebergTableMaintenance {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder.getOrCreate()

    // 清理过期快照
    expireSnapshots(spark, 5L)
    removeOrphanFiles(spark)
    compactDataFiles(spark, 128L)
    rewriteManifests(spark, 10L)
  }

  /**
   * 清理过期快照 避免快照过多
   * @param spark sparkSession
   * @param snapRetainMinutes 快照保留时间
   */
  def expireSnapshots(spark: SparkSession, snapRetainMinutes: Long): Unit = {
    val tsToExpire = System.currentTimeMillis() - (1000 * 60 * snapRetainMinutes)  //设置清理时间
    val catalog = new HiveCatalog()
    catalog.setConf(spark.sessionState.newHadoopConf)
    val table = catalog.loadTable(TableIdentifier.of("iceberg_db", "nt_order_detail"))
    val result = SparkActions.get().expireSnapshots(table).expireOlderThan(tsToExpire).execute()
    println(s"#ExpireSnapshots Delete Data Files Count:${result.deletedDataFilesCount()}")
    println(s"#ExpireSnapshots Delete Manifest Files Count:${result.deletedManifestsCount()}")
  }

  /**
   * 清理孤立文件
   * In Spark and other distributed processing engines, task or job failures can leave files that are not referenced by table metadata, and in some cases normal snapshot expiration may not be able to determine a file is no longer needed and delete it.
   * @param spark sparkSession
   */
  def removeOrphanFiles(spark: SparkSession): Unit = {
    val catalog = new HiveCatalog()
    catalog.setConf(spark.sessionState.newHadoopConf)
    val table = catalog.loadTable(TableIdentifier.of("iceberg_db", "nt_order_detail"))
    val result = SparkActions.get().deleteOrphanFiles(table).execute()
    result.orphanFileLocations().forEach(println)
  }

  /**
   * 并行合并数据文件
   * @param spark sparkSession
   * @param targetFileSizeMb 合并后的目标文件大小
   * Iceberg tracks each data file in a table. More data files leads to more metadata stored in manifest files, and small data files causes an unnecessary amount of metadata and less efficient queries from file open costs.
   */
  def compactDataFiles(spark: SparkSession, targetFileSizeMb: Long): Unit = {
    val catalog = new HiveCatalog()
    catalog.setConf(spark.sessionState.newHadoopConf)
    val table = catalog.loadTable(TableIdentifier.of("iceberg_db", "nt_order_detail"))
    val result = SparkActions
      .get()
      .rewriteDataFiles(table)
      .filter(Expressions.equal("date", "2020-08-18"))
      .option("target-file-size-bytes", (targetFileSizeMb * 1024 * 1024).toString)
      .execute()
    result.rewriteResults().forEach(println)
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
  def rewriteManifests(spark: SparkSession, rewriteThresholdMb: Long): Unit = {
    val catalog = new HiveCatalog()
    catalog.setConf(spark.sessionState.newHadoopConf)
    val table = catalog.loadTable(TableIdentifier.of("iceberg_db", "nt_order_detail"))
    val rewritePredicate: java.util.function.Predicate[ManifestFile] = ((file: ManifestFile) => file.length() < rewriteThresholdMb * 1024 * 1024)
    val result = SparkActions
      .get()
      .rewriteManifests(table)
      .rewriteIf(rewritePredicate) // 10 MB
      .execute()
    result.rewrittenManifests().forEach(println)
  }





}
