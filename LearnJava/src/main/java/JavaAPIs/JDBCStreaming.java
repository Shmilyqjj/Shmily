package JavaAPIs;

import com.google.common.util.concurrent.RateLimiter;

import java.sql.*;

/**
 * JDBC 流式读取
 */
public class JDBCStreaming {
    public static void main(String[] args) throws SQLException {
        Connection connection;
        String driver = "com.mysql.cj.jdbc.Driver";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?characterEncoding=utf8&useCursorFetch=true&useSSL=false&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&allowMultiQueries=true&connectTimeout=6000&socketTimeout=80000&netTimeoutForStreamingResults=60&zeroDateTimeBehavior=convertToNull", "root", "123456");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        assert connection != null;
        PreparedStatement pstmt = connection.prepareStatement("select id from test.qjj");

        if ("org.apache.hive.jdbc.HiveDriver".equals(driver)) {
            pstmt.setFetchSize(200);
        } else {
            pstmt.setFetchSize(Integer.MIN_VALUE);
        }


        ResultSet resultSet = pstmt.executeQuery();

        int qps = 20000;
        int rowCount = 0;
        // 高并发限流  可以限制拉取速度  避免拉崩数据库
        RateLimiter rateLimiter = RateLimiter.create(qps);
        while (resultSet.next()) {
            rateLimiter.acquire();
            String s = resultSet.getString(1);
            System.out.println(s);
            rowCount ++;

            if (rowCount % 100000 == 0) {
                System.out.println("running rowCount:" + rowCount);
            }
        }

    }
}
