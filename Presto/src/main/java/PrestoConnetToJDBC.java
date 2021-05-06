import java.sql.*;
import java.util.Properties;

/**
 * :Description: Prest JDBC连接
 * :Author: 佳境Shmily
 * :Create Time: 2021/3/20 20:19
 * :Site: shmily-qjj.top
 *
 * URL：
 * jdbc:presto://host:port
 * jdbc:presto://host:port/catalog
 * jdbc:presto://host:port/catalog/schema
 *
 * example: jdbc:presto://cdh101:8080/hive/staging_db_users
 *
 * references:https://prestodb.io/docs/current/installation/jdbc.html
 */
public class PrestoConnetToJDBC {
    public static void main(String[] args) throws SQLException {
        // 1.简单创建连接
//        String url = "jdbc:presto://cdh101:8080/hive/staging_db_users";
//        Connection connection = DriverManager.getConnection(url, "root", null);
//        connection.prepareStatement("show tables");

        // 2.带参数创建连接
        String url = "jdbc:presto://cdh101:8080/hive/staging_db_users";
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "");
        properties.setProperty("SSL", "false");
        Connection connection = DriverManager.getConnection(url, properties);

        // 3.带参数创建连接
//          String url = "jdbc:presto://cdh101:8080/hive/staging_db_users?user=root&password=secret&SSL=true";
//          Connection connection = DriverManager.getConnection(url);

        // 读数据或做其他操作
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from tb_user_info limit 10");
            while (rs.next()){
                System.out.println(rs.getString(1) + "--" + rs.getString(2));
            }

    }
}
