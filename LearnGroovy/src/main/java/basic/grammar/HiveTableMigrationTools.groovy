import com.sun.org.apache.xpath.internal.operations.Bool
import groovy.sql.Sql

import java.sql.Connection

/**
 * 从旧集群迁移指定Hive表到新集群
 * groovy -cp "/hadoop/bigdata/common/lib/*" HiveTableMigrationTools.groovy  db1.tableName1 db1.tableName2 db2.tableName..N
 * Author:Shmily
 */


class HiveTableMigrationTools {
    static void main(String[] tables) {
        def old_hive_url="jdbc:hive2://source_hs2_ip:source_hs2_port/default"
        def new_hive_url="jdbc:hive2://target_hs2_ip:target_hs2_port/default"
        def hive_user="user"
        def hive_password="pwd"
        String src_hdfs_addr="hdfs://source_hdfs_nameservice_name"
        String tgt_hdfs_addr="hdfs://target_activeNN_ip:target_activeNN_port"
        String src_hdfs_ns = "nameservice-hive"
        String tgt_hdfs_ns = "nameservice1"
        def oldConn = getHiveConn(old_hive_url, hive_user, hive_password)
        def newConn = getHiveConn(new_hive_url, hive_user, hive_password)
        tables.each {table ->
            println("#################Hive表${table}开始迁移####################")
            Migrate(oldConn, newConn, table, src_hdfs_addr, tgt_hdfs_addr, src_hdfs_ns, tgt_hdfs_ns)
            println("#################Hive表${table}已成功迁移到新集群####################")
        }
    }

    static def getHiveConn(String hive_url, String hive_user, String hive_password){
        try{
            return Sql.newInstance(hive_url, hive_user, hive_password, "org.apache.hive.jdbc.HiveDriver")
        }catch(Exception ex){
            println("[Error]Failed to init hive conn with url: ${hive_url}")
            System.exit(1)
        }
    }

    static void Migrate(oldConn, newConn, tableName, String srcHDFSAddr, String tgtHDFSAddr, String srcHDFSNs, String tgtHDFSNs){
        String createSQL = oldConn.firstRow("show create table " + tableName).get("createtab_stmt")
        // 先删除新集群该表
        try {
            newConn.execute("drop table if exists " + tableName)
        }catch (Exception e){
            if(e.toString().contains("Cannot drop a view with DROP TABLE")){
                // 如果是视图就创建视图
                newConn.execute("drop view " + tableName)
                newConn.execute(createSQL.replace(srcHDFSNs, tgtHDFSNs))
                return
            }
            else{
                throw e
            }
        }
        // println(createSQL)


        // 获取表存储路径及是否分区表
        String path = ""
        boolean isPartitionTable = false
        oldConn.eachRow("desc formatted " + tableName) {
            if (it.getString(1).trim() == "# Partition Information") {
                isPartitionTable = true
            }
            if (it.getString(1).trim() == "Location") {
                path = it.getString(2).trim()
            }
        }

        // 先删除新集群数据目录 避免建表报错
        tableDataDelete(path, srcHDFSAddr, tgtHDFSAddr)
        // 新集群建表
        newConn.execute(createSQL.replace(srcHDFSNs, tgtHDFSNs))

        // 数据迁移
        if (path){
            dataMigration(path, srcHDFSAddr, tgtHDFSAddr)
        }else {
            println("[Error]Hive table dataMigration failed path ${path} does not exist.")
        }

        // 分区表修复分区
        if (isPartitionTable){
            newConn.execute("msck repair table " + tableName)
            println("[INFO]Partition table ${tableName} msck-repair succeed.")
        }

    }

    static void dataMigration(String tableHDFSPath, String srcHDFSUrl, String tgtHDFSUrl){
        String tgt_table_path = tableHDFSPath.replace(srcHDFSUrl, tgtHDFSUrl)
//        println("${tableHDFSPath} => ${tgt_table_path}")
        String cmd = "hadoop distcp -D ipc.client.fallback-to-simple-auth-allowed=true -prbugp -i -m 50 -update -delete -skipcrccheck -bandwidth 100 -strategy dynamic ${tableHDFSPath} ${tgt_table_path}"
        try{
            println("[INFO]Running distcp[${tableHDFSPath}] please wait...")
            def proc = cmd.execute()
            proc.waitFor()  // 等待shell执行完成
            println("[INFO]Path ${tableHDFSPath} distcp to new cluster succeed.")
        }catch(Exception ex){
            println("[Error]Failed to migrating hdfs data path ${tableHDFSPath}")
        }
    }

    static void tableDataDelete(String tableHDFSPath, String srcHDFSUrl, String tgtHDFSUrl){
        String tgt_table_path = tableHDFSPath.replace(srcHDFSUrl, tgtHDFSUrl)
        String cmd = "hdfs dfs -rm -r -f ${tgt_table_path}"
        try{
            def proc = cmd.execute()
            proc.waitFor()
            println("[INFO]Path ${tgt_table_path} on new cluster delete succeed.")
        }catch(Exception ex){
            println("[Error]Failed to delete hdfs data path ${tgt_table_path}")
        }
    }
}



