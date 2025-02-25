package kyuubi.jdbc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.sql.*;

/**
 * @author Shmily
 * @Description: kyuubi jdbc test
 * @CreateTime: 2024/8/20 下午10:46
 */

public class JDBCTest {
    private static String driver = "org.apache.hive.jdbc.HiveDriver";
    private static String url = "jdbc:hive2://zkIp1:2181,zkIp2:2181,zkIp3:2181/default;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=kyuubi;principal=hive/_HOST@xldw.xunlei.com;hive.server2.proxy.user=someone?";
    private static String krb5 = "/etc/krb5.conf";
    private static String keytab = "/path/to/hive.keytab";
    private static String principal = "hive/xx@REALM.COM";

    public static void main(String[] args) throws Exception {
        System.setProperty("java.security.krb5.conf",krb5);
        Configuration configuration = new Configuration();
        configuration.setBoolean("hadoop.security.authorization", true);
        configuration.set("hadoop.security.authentication","Kerberos");
        UserGroupInformation.setConfiguration(configuration);
        try {
            if (UserGroupInformation.isLoginKeytabBased()) {
                UserGroupInformation.getLoginUser().reloginFromKeytab();
            } else{
                UserGroupInformation.loginUserFromKeytab(principal, keytab);
            }
            System.out.println("ticketCache=====>"+UserGroupInformation.isLoginTicketBased());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String sql = "show databases;";
        ResultSet res = null;
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url);
        Statement st = conn.createStatement();
        String[] splits = sql.split(";");
        for (String split:splits){
            res = st.executeQuery(split);
            while (res.next()){
                System.out.println(res.getString(1));
            }
        }
        if (res != null) {
            res.close();
        }
        st.close();
        conn.close();
    }
}
