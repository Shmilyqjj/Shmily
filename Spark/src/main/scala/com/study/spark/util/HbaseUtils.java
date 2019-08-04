package com.study.spark.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * 操作Hbase的工具类
 * 爱奇艺影视数据分析Spark项目
 * 工具类一般是单例模式 单实例
 */
public class HbaseUtils {
    HBaseAdmin admin = null;
    Configuration conf = null;
    private HbaseUtils(){    //初始化工具类（通过私有构造方法）  单例只能通过名字获取里面的私有方法
        conf = new Configuration();
        conf.set("hbase.zookeeper.quorum","hadoop101:2181");
        conf.set("hbase.rootdir","hdfs://hadoop101/hbase");

        try {
            admin = new HBaseAdmin(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static HbaseUtils instance = null;  //设置取该类的私有变量instance
    public static synchronized  HbaseUtils getInstance(){  //获得该类的公有方法 - 该方法静态的可直接通过类名调用
        if(null == instance){
            instance = new HbaseUtils();
        }
        return instance;
    }


    /**
     * 根据表名获取Htable表的实例化对象
     * @param tableName 表名
     * @return
     */
    public HTable getHtable(String tableName){
        HTable table = null;
        try {
            table = new HTable(conf,tableName);
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
        HTable table = getHtable(tableName);
        Put put = new Put(Bytes.toBytes(rowKey)); //创建Put对象先给它一个rowkey - 初始化
        put.add(Bytes.toBytes(cf),Bytes.toBytes(column),Bytes.toBytes(value));
        //提交Put对象 完成插入
        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    //测试类 测试是否成功：
    public static void main(String[] args) {
        String tableName = "category_clickcount";
        String rowkey = "20190804_1"; //rowkey的格式设计是：日期_类别编号
        String cf = "info";
        String column = "category_clickcount";
        String value = "100";

        HbaseUtils.getInstance().put(tableName,rowkey,cf,column,value);
        //插入成功

    }


}
