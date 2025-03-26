package mysql.jdbc;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shmily
 * @Description: MySQL JDBC 动态参数测试
 * @Usage java -cp JDBC-1.0-jar-with-dependencies.jar mysql.jdbc.JDBCTest --url "jdbc:mysql://..." --user "root" --password "pwd" --sql "show databases;select 1;"
 */
public class JDBCTest {

    public static void main(String[] args) throws Exception {
        // 解析命令行参数
        Map<String, String> params = parseArgs(args);

        // 检查必要参数
        if (!params.containsKey("url") || !params.containsKey("user")) {
            System.err.println("Missing required parameters! Usage:");
            System.err.println("java -cp JDBC-1.0-jar-with-dependencies.jar mysql.jdbc.JDBCTest --url <JDBC_URL> --user <USER> --password <PWD> --sql <SQL_STATEMENTS>");
            System.exit(1);
        }

        // 获取参数值
        String url = params.get("url");
        String user = params.get("user");
        String password = params.getOrDefault("password", ""); // 密码可选
        String sql = params.getOrDefault("sql", "show databases;"); // 默认SQL

        // 执行数据库操作
        executeQuery(url, user, password, sql);
    }

    /**
     * 执行SQL查询
     */
    private static void executeQuery(String url, String user, String password, String sql)
            throws ClassNotFoundException, SQLException {

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            st = conn.createStatement();

            // 分割并执行多个SQL语句
            for (String statement : sql.split(";")) {
                if (statement.trim().isEmpty()) continue;

                System.out.println("\nExecuting: " + statement.trim());
                rs = st.executeQuery(statement);
                printResultSet(rs);
                rs.close();
            }
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (conn != null) conn.close();
        }
    }

    /**
     * 打印查询结果
     */
    private static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        // 打印表头
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(meta.getColumnName(i) + "\t");
        }
        System.out.println("\n----------------------------------");

        // 打印数据
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rs.getString(i) + "\t");
            }
            System.out.println();
        }
    }

    /**
     * 解析命令行参数
     */
    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    params.put(key, args[++i]);
                } else {
                    params.put(key, "");
                }
            }
        }
        return params;
    }
}