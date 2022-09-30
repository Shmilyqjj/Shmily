package top.shmily_qjj.iceberg.table.maintenance;

import org.apache.hadoop.conf.Configuration;
import org.apache.iceberg.Table;
import org.apache.iceberg.hadoop.HadoopTables;

public class IcebergTableMaintenance {
    private static final HadoopTables TABLES = new HadoopTables(new Configuration());
    public static void main(String[] args) {

    }

    public static void expireSnapshots(){
        Table table = TABLES.load("oss://bucket/user/iceberg/warehouse");
        long tsToExpire = System.currentTimeMillis() - (1000 * 60 * 60 * 24); // 1 day
        table.expireSnapshots().expireOlderThan(tsToExpire).commit();
    }


}
