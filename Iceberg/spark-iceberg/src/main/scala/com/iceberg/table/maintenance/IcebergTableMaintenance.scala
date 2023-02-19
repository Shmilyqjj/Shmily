package com.iceberg.table.maintenance

import com.google.common.collect.ImmutableMap
import com.typesafe.scalalogging.Logger
import org.apache.hadoop.conf.Configuration
import org.apache.iceberg.{ManifestFile, Table}
import org.apache.iceberg.catalog.TableIdentifier
import org.apache.iceberg.hadoop.HadoopCatalog
import org.apache.iceberg.hive.HiveCatalog
import org.apache.iceberg.spark.actions.{RewriteDataFilesSparkAction, SparkActions}
import org.apache.spark.sql.SparkSession

import java.io.File
import java.io.IOException
import com.google.gson.Gson
import org.apache.commons.io.FileUtils
import org.apache.hadoop.fs.Path
import org.apache.iceberg.exceptions.NoSuchTableException

import java.util
import java.util.concurrent.{Executors, TimeUnit}



case class IcebergTableConf(catalog_type: String,
                            warehouse: String,
                            target_datafile_mb: String,
                            filter: String,
                            snap_retain_num: String,
                            snap_retain_minutes: String,
                            rewrite_manifest_mb: String,
                            remove_orphan_files:String,
                            interval_minutes:String)
case class IcebergManagerConf(tables: util.HashMap[String, IcebergTableConf],
                              hadoop_config: String,
                              krb5_path: String,
                              keytab_path: String,
                              login_user: String,
                              metastore_uri: String,
                              default_warehouse: String,
                              pool_size: String)


object IcebergTableMaintenance {
  private[this] val logger = Logger(this.getClass)
  private var confPath = "./iceberg-manager-conf.json"
  private var krb5_path: String = ""
  private var keytab_path: String = ""
  private var login_user: String = ""
  private var metastore_uri: String = ""
  private var default_warehouse: String = ""
  private var hadoop_config: String = ""
  private var pool_size: Int = 6

  def main(args: Array[String]): Unit = {
    if(args.length != 0){
      confPath = args(0)
    }
    val tables: util.HashMap[String, IcebergTableConf] = parseConfig(confPath)

    val spark: SparkSession = SparkSession.builder.master("yarn").appName("IcebergTablesMaintenance").getOrCreate()
    val conf = new Configuration
    for (sourceFile <- this.hadoop_config.split(",")) {
      conf.addResource(new Path(sourceFile))
    }
    // kerberos认证
    setKerberos(conf, this.krb5_path, this.keytab_path, this.login_user)

    // 解决timestamp类型数据无法处理的问题
    spark.sql("SET `spark.sql.iceberg.handle-timestamp-without-timezone`=`true`")

    // 提交执行清理合并任务
    val executor = Executors.newFixedThreadPool(this.pool_size)
    logger.info(s"Initialized a executor with ${this.pool_size} threads.")


    tables.entrySet().forEach(e => executor.execute(() => {
      execTableClear(spark, conf, e.getKey, e.getValue)
    }))

    executor.shutdown()
    while (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
      logger.debug("Waiting for task done.")
    }
//    tables.entrySet().forEach(e => execTableClear(spark, conf, e.getKey, e.getValue))

  }

  /**
   * 初始化配置 表配置文件提取
   * @param confPath 配置文件路径
   * @return
   */
  def parseConfig(confPath: String): util.HashMap[String, IcebergTableConf] = {
    val file: File = new File(confPath)
    if (!file.exists()){
      throw new IOException("Config file not exists.")
    }
    val content: String = FileUtils.readFileToString(file, "UTF-8")
    val gson = new Gson()
    val appConf = gson.fromJson(content, classOf[IcebergManagerConf])
    this.krb5_path = appConf.krb5_path
    this.keytab_path = appConf.keytab_path
    this.login_user = appConf.login_user
    this.metastore_uri = appConf.metastore_uri
    this.default_warehouse = appConf.default_warehouse
    this.hadoop_config = appConf.hadoop_config
    this.pool_size = if(appConf.pool_size != null){
      math.min(appConf.pool_size.toInt, appConf.tables.size())
    } else{
      math.min(this.pool_size, appConf.tables.size())
    }
    appConf.tables
  }


  def execTableClear(spark: SparkSession, conf: Configuration, tableName: String, tableConfig: IcebergTableConf): Unit = {
    val warehouse = if(tableConfig.warehouse != null) tableConfig.warehouse else this.default_warehouse
    val db_name = tableName.split("\\.")(0)
    val table_name = tableName.split("\\.")(1)
    val st = System.currentTimeMillis()

    // 获取表对象
    try {
      val table = tableConfig.catalog_type match {
        case "hive" => getTable(conf, this.metastore_uri, warehouse, db_name, table_name)
        case "hadoop" | "location_based_table" => getTable(conf, warehouse, db_name, table_name)
        case _ => throw new NotImplementedError(s"Catalog type ${tableConfig.catalog_type} of table $tableName is not supported.")
      }

      logger.info(s"Start execTableClear[tableName: $tableName]")

      // 清理过期快照
      expireSnapshots(spark, table, tableConfig.snap_retain_minutes.toLong, tableConfig.snap_retain_num.toInt)

      // 合并数据文件到目标大小
      compactDataFiles(spark, table, tableConfig.target_datafile_mb.toLong, tableConfig.filter)

      // 重写Manifest
      rewriteManifests(spark, table, tableConfig.rewrite_manifest_mb.toLong)

      // 清理孤立文件
      if(tableConfig.remove_orphan_files.toBoolean){
        removeOrphanFiles(spark, table)
      }
      logger.info(s"Finished execTableClear[tableName: $tableName] cost: ${(System.currentTimeMillis() - st) / 1000.0}s")
    }catch {
      case ex: NoSuchTableException => logger.error(s"Failed execTableClear[tableName: $tableName] ${ex.toString}")
      case ex: Exception => logger.error(s"Failed execTableClear[tableName: $tableName] ${ex.toString}")
    }

  }

  /**
   * Kerberos认证
   * @param conf
   * @param krb5Path
   * @param keytabPath
   */
  def setKerberos(conf: Configuration, krb5Path: String,keytabPath: String, loginUser: String) : Unit = {
    System.setProperty("java.security.krb5.conf", krb5Path)
    conf.setBoolean("hadoop.security.authentication", true)
    conf.set("hadoop.security.authentication", "Kerberos")
    import org.apache.hadoop.security.UserGroupInformation
    UserGroupInformation.setConfiguration(conf)
    UserGroupInformation.loginUserFromKeytab(loginUser, keytabPath)
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
   * 清理过期快照 避免快照过多 (该过程会删除未被元数据引用的数据文件)
   * @param spark sparkSession
   * @param snapRetainMinutes 快照保留时间
   * @param snapRetainLastMinNum 快照保留数量
   */
  def expireSnapshots(spark: SparkSession, table: Table, snapRetainMinutes: Long, snapRetainLastMinNum: Int): Unit = {
    logger.info("Start ExpireSnapshots job for table " + table.name())
    val st = System.currentTimeMillis()
    val tsToExpire = System.currentTimeMillis() - (1000 * 60 * snapRetainMinutes)  //设置清理时间
    val result = SparkActions.get(spark).expireSnapshots(table).expireOlderThan(tsToExpire).retainLast(snapRetainLastMinNum).execute()
    logger.info(s"ExpireSnapshots Deleted Data Files Count:${result.deletedDataFilesCount()}")
    logger.info(s"ExpireSnapshots Deleted ManifestFiles Count:${result.deletedManifestsCount()}")
    logger.info(s"ExpireSnapshots Deleted ManifestLists Count:${result.deletedManifestListsCount()}")
    logger.info(s"ExpireSnapshots Deleted EqualityDeleteFiles Count:${result.deletedEqualityDeleteFilesCount()}")
    logger.info(s"ExpireSnapshots Deleted PositionDeleteFiles Count:${result.deletedPositionDeleteFilesCount()}")
    logger.info(s"ExpireSnapshots[${table.name()}] finished cost: ${(System.currentTimeMillis() - st) / 1000.0}s")
  }

  /**
   * 清理孤立文件
   * In Spark and other distributed processing engines, task or job failures can leave files that are not referenced by table metadata,
   * and in some cases normal snapshot expiration may not be able to determine a file is no longer needed and delete it.
   * @param spark sparkSession
   */
  def removeOrphanFiles(spark: SparkSession, table: Table): Unit = {
    logger.info("Start RemoveOrphanFiles job for table " + table.name())
    val st = System.currentTimeMillis()
    val result = SparkActions.get(spark).deleteOrphanFiles(table).execute()
    logger.info("Removed orphan files: " + List(result.orphanFileLocations()).mkString(","))
    logger.info(s"RemoveOrphanFiles[${table.name()}] finished cost: ${(System.currentTimeMillis() - st) / 1000.0}s")
  }

  /**
   * 并行合并数据文件 合并小文件
   * @param spark sparkSession
   * @param targetFileSizeMb 合并后的目标文件大小
   * Iceberg tracks each data file in a table. More data files leads to more metadata stored in manifest files,
   * and small data files causes an unnecessary amount of metadata and less efficient queries from file open costs.
   * TODO: 该方法是合并全表数据文件的,会扫描全表所有分区并按分区合并,是串行执行的;每次执行都扫描全部数据文件,代价很大,随着分区和文件数增多,耗时越来越长;可以优化为按分区合并,每次只扫描指定范围分区的数据;可利用filter(Expressions.equal("ds", "20221130"))划定合并范围
   */
  def compactDataFiles(spark: SparkSession, table: Table, targetFileSizeMb: Long, filters: String): Unit = {
    logger.info("Start CompactDataFiles job for table " + table.name())
    val st = System.currentTimeMillis()
    val action: RewriteDataFilesSparkAction = SparkActions
      .get(spark)
      .rewriteDataFiles(table)

    def applyFilters(filtersString: String, action: RewriteDataFilesSparkAction): RewriteDataFilesSparkAction = {
      if (!"".equals(filtersString)){
        filtersString.split(",")
        // TODO: add filters
        action
      }else{
        action
      }
    }

    val result =
      action
    //      .filter(Expressions.equal("date", "2020-08-18"))
      .option("target-file-size-bytes", (targetFileSizeMb * 1024 * 1024).toString)
      .option("rewrite-all", "true")  // 避免清理得不干净 如果不加该参数 清理后可能还会有部分小文件
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
      logger.info(s"CompactDataFiles[${table.name()}] finished cost: ${(System.currentTimeMillis() - st) / 1000.0}s")
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
    val st = System.currentTimeMillis()
    val rewritePredicate: java.util.function.Predicate[ManifestFile] = ((file: ManifestFile) => file.length() < rewriteThresholdMb * 1024 * 1024)
    val result = SparkActions
      .get(spark)
      .rewriteManifests(table)
      .rewriteIf(rewritePredicate) // 10 MB
      .execute()
    result.rewrittenManifests().forEach(m => logger.info(s"Rewritten manifest file: ${m.path()}"))
    result.addedManifests().forEach(m => logger.info(s"Added manifest file: ${m.path()}"))
    logger.info(s"RewriteManifests[${table.name()}] finished cost: ${(System.currentTimeMillis() - st) / 1000.0}s")
  }

}
