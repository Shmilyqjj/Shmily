package top.qjj.shmily.kudu.app;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.AlterTableOptions;
import org.apache.kudu.client.CreateTableOptions;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;

/**
 * :Description: Kudu util
 * :Author: 佳境Shmily
 * :Create Time: 2021/8/22 19:08
 * :Site: shmily-qjj.top
 */
public class KuduUtil {
    //待完善
}



class KuduOperations {
    private static volatile KuduOperations instance;
    private KuduClient kuduClient = null;
    private KuduOperations(String masterAddr){
        kuduClient = new KuduClient.KuduClientBuilder(masterAddr).defaultOperationTimeoutMs(6000).build();
    }

    public static KuduOperations getInstance(String masterAddr){
        if(instance == null){
            synchronized (KuduOperations.class){
                if(instance == null){
                    instance = new KuduOperations(masterAddr);
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
     * 创建表
     * 注意：Impala DDL对表字段名大小写不敏感，但Kudu层已经转为小写，且Kudu API中字段名必须小写；
     * 注意：Impala DDL建表表名大小写敏感且到Kudu层表名不会被转成小写，且Kudu API对表名大小写敏感。
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