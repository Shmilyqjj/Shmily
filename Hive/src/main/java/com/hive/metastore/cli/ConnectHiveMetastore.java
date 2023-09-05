package com.hive.metastore.cli;

import org.apache.hadoop.conf.Configuration;


import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.RetryingMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 连接HiveMetaStore
 */
public class ConnectHiveMetastore {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConnectHiveMetastore.class);
    public static void main(String[] args) throws TException, IOException {
        HiveConf conf = new HiveConf();
        conf.set("hive.metastore.uris", "thrift://shmily:9083");
        String krb5File = "/etc/krb5.conf";
        String keyUser = "hive";
        String keyPath = "/opt/keytabs/hive.keytab";
        String keyPrincipal = "hive/shmily@SHMILY-QJJ.TOP";

        IMetaStoreClient client = ConnectHiveMetastore.init(conf, krb5File, keyUser, keyPath, keyPrincipal);

        System.out.println("----------------------------获取所有catalogs-------------------------------------");
        client.getCatalogs().forEach(System.out::println);

        System.out.println("------------------------获取catalog为hive的描述信息--------------------------------");
        System.out.println(client.getCatalog("hive").toString());

        System.out.println("--------------------获取catalog为hive的所有database-------------------------------");
        client.getAllDatabases("hive").forEach(System.out::println);
        System.out.println("--------------------获取catalog为hive,database为default下的所有table-------------------------------");
        client.getAllTables("default").forEach(System.out::println);

        System.out.println("--------------------修改表注释及字段的注释-------------------------------");
        Table table = client.getTable("db_name", "table_name");
        // 表注释
        Map<String, String> parameters = table.getParameters();
        parameters.put("comment","表注释");
        table.setParameters(parameters);

        // 字段注释
        StorageDescriptor sd = table.getSd();
        List<FieldSchema> cols = sd.getCols();
        List<FieldSchema> afterModifyColumns = cols.stream().map(col -> {
            if ("要修改的字段".equals(col.getName())) {
                col.setComment("要修改的comment");
            }
            return col;
        }).collect(Collectors.toList());
        sd.setCols(afterModifyColumns);
        table.setSd(sd);
        client.alter_table("qjj_test", "qjj_test", table);

    }

    /**
     * 初始化HMS连接  连接无Kerberos认证的HMS
     * @param conf org.apache.hadoop.hive.conf.HiveConf 已经带有thrift.uris参数
     * @return IMetaStoreClient
     * @throws MetaException 异常
     * 连接Kerberos HMS会抛org.apache.thrift.transport.TTransportException
     */
    public static IMetaStoreClient init(HiveConf conf) throws MetaException {
        try {
            return RetryingMetaStoreClient.getProxy(conf, false);
        } catch (MetaException e) {
            LOGGER.error("连接HiveMetastore失败", e);
            throw e;
        }
    }


    /**
     * 初始化HMS连接 带Kerberos认证的HMS
     * @param conf org.apache.hadoop.hive.conf.HiveConf  已经带有thrift.uris参数
     * @param principal kerberos票据
     * @return IMetaStoreClient
     * @return
     * @throws MetaException
     */
    public static IMetaStoreClient init(HiveConf conf, String krb5File, String keyUser, String keytabPath, String principal) throws MetaException, IOException {
        try {
            //Login via kerberos
            LOGGER.info("Login with kerberos(User: " + keyUser + ", Krb5File:" + krb5File + ", Keytab: " + keytabPath + ", Principal: " + principal + ")");
            System.setProperty("java.security.krb5.conf", krb5File); //可以直接在启动脚本里面设置 export xxx=xxx
            System.setProperty("krb.principal", keyUser);
            Configuration hadoopConf = new Configuration();
            hadoopConf.set("hadoop.security.authentication", "kerberos");
            hadoopConf.set("kerberos.principal", principal);
            UserGroupInformation.setConfiguration(hadoopConf);
            UserGroupInformation.loginUserFromKeytab(keyUser, keytabPath);
            LOGGER.info("Login successful for user " + UserGroupInformation.getCurrentUser());
            // Update HiveConf
            conf.setVar(HiveConf.ConfVars.METASTORE_KERBEROS_PRINCIPAL, principal);
            conf.setVar(HiveConf.ConfVars.METASTORE_USE_THRIFT_SASL, "true");
            conf.setVar(HiveConf.ConfVars.METASTORE_CLIENT_SOCKET_TIMEOUT, "120s");
//            conf.setVar(HiveConf.ConfVars.METASTOREURIS, "thrift://shmily:9083");
            conf.setVar(HiveConf.ConfVars.METASTORE_CLIENT_CONNECT_RETRY_DELAY, "2s");
        } catch (Throwable ex) {
            LOGGER.error("Login failed,err: " + ex);
            throw ex;
        } finally {
            LOGGER.debug("Login end.");
        }

        try {
            return RetryingMetaStoreClient.getProxy(conf, false);
        } catch (MetaException e) {
            LOGGER.error("连接HiveMetastore失败", e);
            throw e;
        }
    }








}
