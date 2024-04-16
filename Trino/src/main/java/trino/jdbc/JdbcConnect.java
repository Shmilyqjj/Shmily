package trino.jdbc;

import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class JdbcConnect {

    private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    public static void main(String[] args) {
        jdbc();
    }

    public static void jdbc() {
        String url = "jdbc:trino://trino_host:7778/hive/default?SSL=true&SSLKeyStorePath=/home/shmily/Tools/trino/trino_key.jks&SSLKeyStorePassword=trino@admin";
        // 数据库用户名和密码（如果需要）
        String user = "username";
        String password = "password";

        // 定义连接对象
        Connection connection = null;
        // 定义 Statement 对象
        Statement statement = null;

        try {
            // 注册 Trino JDBC 驱动程序（可选，如果使用合适的类加载器可能自动注册）
            Class.forName("io.trino.jdbc.TrinoDriver");

            // 建立连接
            connection = DriverManager.getConnection(url, user, password);

            // 创建 Statement 对象
            statement = connection.createStatement();

            // 执行查询
            String query = "select json_parse(col) as col_json from db.table where ds = '20240411' and extdata is not null limit 1";
            ResultSet rs = statement.executeQuery(query);

            // 处理结果集

            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                // 从结果集中获取列的值
                Object v = rs.getObject(1);
                JSONObject fastJsonRecord = new JSONObject();


                fastJsonRecord.put("extdata", v);
                System.out.println(fastJsonRecord);

                record.put("extdata", v);
                System.out.println(gson.toJson(record));
            }

            // 关闭结果集
            rs.close();




        } catch (ClassNotFoundException e) {
            System.err.println("Trino JDBC 驱动程序未找到: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL 错误: " + e.getMessage());
        } finally {
            // 关闭 Statement 对象和连接
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.err.println("关闭 Statement 时出错: " + e.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("关闭连接时出错: " + e.getMessage());
                }
            }
        }
    }


}
