package hms.krb;

import hms.krb.common.PropertyUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.security.UserGroupInformation;

import java.util.List;
import java.util.Map;

/**
 * Description: Kerberos认证 HMS
 * CreateTime: 2025/6/5 14:46
 * Author Shmily
 * Usage: java -cp Kerberos-1.0-SNAPSHOT.jar hms.krb.Main --realm "XXX.COM" --kdc "KDC_IP" --loginUser etl --keytabPath "/path/to/etl.keytab" --metastore "thrift://hms_addr:9083" --metastorePrincipal "hive/_HOST@XXX.COM"
 */

public class Main {
    public static void main(String[] args) throws Exception {
        Map<String, String> params = PropertyUtils.parseArgs(args);

        String realm = params.get("realm");
        String kdc = params.get("kdc");
        String loginUser = params.get("loginUser");
        String keytabPath = params.get("keytabPath");
        String metastore = params.get("metastore");
        String metastorePrincipal = params.get("metastorePrincipal");
        System.out.println("params: " + params);

        System.setProperty("java.security.krb5.realm", realm);
        System.setProperty("java.security.krb5.kdc", kdc);
        System.setProperty("sun.security.krb5.debug", "true");

        Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("hadoop.security.authorization", "true");

        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab(String.format("%s@%s", loginUser, realm), keytabPath);

        HiveConf hiveConf = new HiveConf(conf, Configuration.class);
        hiveConf.set("hive.metastore.uris", metastore);
        hiveConf.set("hive.metastore.sasl.enabled", "true");
        hiveConf.set("hive.metastore.kerberos.principal", metastorePrincipal);

        HiveMetaStoreClient hiveMetaStoreClient = new HiveMetaStoreClient(hiveConf);
        List<String> databaseList = hiveMetaStoreClient.getAllDatabases();

        System.out.println("databases: ");
        databaseList.forEach(System.out::println);
    }
}