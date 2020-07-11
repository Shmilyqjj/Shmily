package top.qjj.shmily.operations;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.*;
import org.apache.kudu.shaded.com.google.common.collect.ImmutableList;
import java.util.LinkedList;

/**
 * :Description: Kudu DLL数据定义 Create Alter Drop 操作表
 * :Author: 佳境Shmily
 * :Create Time: 2020/7/11 20:23
 * :Site: shmily-qjj.top
 */
public class KuduDDL{
    public static void main(String[] args) {
        String kuduMasterAddrs = "cdh102,cdh103,cdh104";
        KuduDDLOperations kuduDDLOperations = KuduDDLOperations.getInstance(kuduMasterAddrs);

        //创建Kudu表
        String tableName = "kudu_table_with_hash";
        //1.Schema指定
        LinkedList<ColumnSchema> schemaList = new LinkedList<>();
        schemaList.add(kuduDDLOperations.newColumn("id", Type.INT32, true));
        schemaList.add(kuduDDLOperations.newColumn("name", Type.STRING, false));
        Schema schema = new Schema(schemaList);
        //2.设置建表参数-哈希分区
//        CreateTableOptions options = new CreateTableOptions();
//        options.setNumReplicas(1);   //设置存储副本数-必须为奇数否则会抛异常
//        List<String> hashKey = new LinkedList<String>();
//        hashKey.add("id");
//        options.addHashPartitions(hashKey,2);  //哈希分区 设置哈希键和桶数
        //2.设置建表参数-Range分区
        CreateTableOptions options = new CreateTableOptions();
        options.setRangePartitionColumns(ImmutableList.of("id")); //设置id为Range key
        int temp = 0;
        for(int i = 0; i < 10; i++){  //id 每10一个区间直到100
            PartialRow lowLevel = schema.newPartialRow();
            lowLevel.addInt("id", temp);  //与字段类型对应 INT32则addInt  INT64则addLong
            PartialRow highLevel = schema.newPartialRow();
            temp += 10;
            highLevel.addInt("id", temp);
            options.addRangePartition(lowLevel, highLevel);
        }
        //3.开始建表
        boolean result = kuduDDLOperations.createTable(tableName, schema, options);
        System.out.println(result);

        //添加字段
        kuduDDLOperations.addColumn(tableName, "test", Type.INT8);

        //删除字段
        kuduDDLOperations.deleteColumn(tableName, "test");

        //删除Kudu表
        boolean delResult = kuduDDLOperations.dropTable(tableName);
        System.out.println(delResult);

        //关闭连接
        kuduDDLOperations.closeConnection();
    }
}

class KuduDDLOperations {
    private static volatile KuduDDLOperations instance;
    private KuduClient kuduClient = null;
    private KuduDDLOperations(String masterAddr){
        kuduClient = new KuduClient.KuduClientBuilder(masterAddr).defaultOperationTimeoutMs(6000).build();
    }

    public static KuduDDLOperations getInstance(String masterAddr){
        if(instance == null){
            synchronized (KuduDDLOperations.class){
                if(instance == null){
                    instance = new KuduDDLOperations(masterAddr);
                }
            }
        }
        return instance;
    }

    public void closeConnection(){
        try {
            kuduClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ColumnSchema newColumn(String name, Type type, boolean isKey){
        ColumnSchema.ColumnSchemaBuilder column = new ColumnSchema.ColumnSchemaBuilder(name, type);
        column.key(isKey);
        return column.build();
    }

    /**
     *
     * @param tableName 表名
     * @param schema Schema信息
     * @param tableOptions 建表参数 TableOptions对象
     * @return boolean
     */
    public boolean createTable(String tableName, Schema schema, CreateTableOptions tableOptions){
        try {
            kuduClient.createTable(tableName, schema, tableOptions);
            System.out.println("Create table successfully!");
            return true;
        } catch (KuduException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删库跑路
     * @param tableName 要删的表名
     * @return boolean 是否需要跑路
     */
    public boolean dropTable(String tableName){
        try {
            kuduClient.deleteTable(tableName);
            System.out.println("Drop table successfully!");
            return true;
        } catch (KuduException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 给Kudu表添加字段
     * @param tableName 表名
     * @param column 字段名
     * @param type 类型
     * @return
     */
    public boolean addColumn(String tableName, String column, Type type) {
        AlterTableOptions alterTableOptions = new AlterTableOptions();
        alterTableOptions.addColumn(new ColumnSchema.ColumnSchemaBuilder(column, type).nullable(true).build());
        try {
            kuduClient.alterTable(tableName, alterTableOptions);
            System.out.println("成功添加字段" + column + "到表" + tableName);
            return true;
        } catch (KuduException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除Kudu表指定字段
     * @param tableName 表名
     * @param column 字段名
     * @return
     */
    public boolean deleteColumn(String tableName, String column){
        AlterTableOptions alterTableOptions = new AlterTableOptions().dropColumn(column);
        try {
            kuduClient.alterTable(tableName, alterTableOptions);
            System.out.println("成功删除表" + tableName + "的字段" + column);
            return true;
        } catch (KuduException e) {
            e.printStackTrace();
        }
        return false;
    }
}
