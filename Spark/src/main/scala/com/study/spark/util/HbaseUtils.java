package com.study.spark.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作Hbase的工具类
 * 爱奇艺影视数据分析Spark项目
 * 工具类一般是单例模式 单实例
 */
public class HbaseUtils {
    Admin admin = null;
    Configuration conf = null;
    private HbaseUtils() {   //初始化工具类（通过私有构造方法）  单例只能通过名字获取里面的私有方法
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "cdh101,cdh102,cdh103");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        try {
            admin = ConnectionFactory.createConnection(conf).getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单例模式   设置取该类的私有变量instance
     */
    private static HbaseUtils instance = null;
    public static synchronized HbaseUtils getInstance(){  //获得该类的公有方法 - 该方法静态的可直接通过类名调用
        if(null == instance){
            instance = new HbaseUtils();
        }
        return instance;
    }

    /**
     * 根据表名获取Htable表的实例化对象
     * @param tableName 表名
     * @return table对象
     */
    public Table getTable(String tableName){
        Table table = null;
        try {
            table = admin.getConnection().getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }


    /**
     * 向Hbase表中插入值的方法put
     * @param tableName 表名
     * @param rowKey 对应key的值
     * @param cf     Hbase的列族 column family
     * @param column 对应的列
     * @param value  对应列的值
     */
    public void put(String tableName,String rowKey,String cf,String column,String value){
        Table table = getTable(tableName);

        Put put = new Put(Bytes.toBytes(rowKey)); //创建Put对象先给它一个rowkey - 初始化

        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(column),Bytes.toBytes(value));
//        put.add(Bytes.toBytes(cf),Bytes.toBytes(column),Bytes.toBytes(value));
        //提交Put对象 完成插入
        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据tableName和day查询数据
     * @param tableName
     * @param day
     * @return Map类型结果
     */
    public Map<String,Long> query(String tableName,String day) throws IOException {
        Table table = getTable(tableName);
        Scan scan = new Scan(); //得到用于扫描region的对象
        ResultScanner resultScanner = table.getScanner(scan);
        Map<String,Long> map = new HashMap<String, Long>();
        for (Result result:resultScanner){
            Cell[] cells = result.rawCells();
            for (Cell cell:cells){
                String rowkeys = Bytes.toString(CellUtil.cloneRow(cell));
                String temp = Bytes.toString(CellUtil.cloneValue(cell));
                Long values = Long.parseLong(temp);
                map.put(rowkeys,values);
            }
        }
        return map;
    }


    //测试--测试是否成功：
    public static void main(String[] args) throws IOException {

        //插入数据测试
//        String tableName = "category_clickcount";
//        String rowkey = "20190805_3"; //rowkey的格式设计是：日期_类别编号
//        String cf = "info";
//        String column = "category_clickcount";
//        String value = "100";
//        HbaseUtils.getInstance().put(tableName,rowkey,cf,column,value);

        //查询数据测试
//        Map<String,Long> map =  HbaseUtils.getInstance().query("category_clickcount","2019");
//        for (Map.Entry<String,Long> entry:map.entrySet()){
//            System.out.println(entry.getKey()+" "+entry.getValue());
//        }
        //插入成功
    }

}
