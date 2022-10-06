package top.shmily_qjj.iceberg.table.maintenance;

import org.apache.hadoop.conf.Configuration;
import org.apache.iceberg.Table;
import org.apache.iceberg.catalog.Catalog;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.hadoop.HadoopCatalog;


public class IcebergTableMaintenance {
    public static void main(String[] args) {
        Configuration conf = new Configuration();
        String hadoopResources = "E:\\CDH-Conf\\core-site.xml,E:\\CDH-Conf\\hdfs-site.xml";
        for (String confFile:hadoopResources.split(",")) {
            conf.addResource(confFile);
        }

        String warehouseLocation = "hdfs://cdh102:8020/user/iceberg/warehouse";
        String db = "iceberg_db";
        String tableName = "hadoop_iceberg_partitioned_table";


        Catalog catalog = new HadoopCatalog(conf, warehouseLocation);
        Table table = catalog.loadTable(TableIdentifier.of(db, tableName));
        expireSnapshots(table, 10L);
    }

    public static void expireSnapshots(Table table, Long snapRetainMinutes){
        long tsToExpire = System.currentTimeMillis() - (60 * 1000 * snapRetainMinutes);
        System.out.println("使timestamp为" + tsToExpire + "时间前的快照过期");
        table.expireSnapshots().expireOlderThan(tsToExpire).commit();
//        table.expireSnapshots().retainLast(2).commit();
    }


}
