/**
 * Test HBase
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.IOException;

public class HbaseTest {

    private static Configuration conf = null;
    private static Connection connection = null;
    private static HBaseAdmin admin = null;
    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "192.168.1.101");
        try {
            connection = ConnectionFactory.createConnection(conf);
            admin = (HBaseAdmin)connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean isTableExist(String tableName) throws IOException {
////        Configuration conf = new HBaseConfiguration();
//        Configuration conf = HBaseConfiguration.create();
//                conf.set("hbase.zookeeper.quorum", "192.168.1.101");
//        Connection connection = ConnectionFactory.createConnection(conf);
////        HBaseAdmin admin = new HBaseAdmin(conf);
//        HBaseAdmin admin = (HBaseAdmin)connection.getAdmin();
        return admin.tableExists(tableName);
    }

    public static void createTable(String tableName,String... cfs) throws IOException {

        if(isTableExist(tableName)){
            System.out.println("表已存在");
            return;
        }
        HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
        for (String cf : cfs) {
            HColumnDescriptor hc = new HColumnDescriptor(cf);
            hTableDescriptor.addFamily(hc);
        }
        admin.createTable(hTableDescriptor);

    }



    public static void main(String[] args) throws IOException {
//        System.out.println(isTableExist("student"));
//        System.out.println(isTableExist("student111"));

        createTable("teacher","info");
    }
}