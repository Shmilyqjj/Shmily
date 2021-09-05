package basic.grammar
/**
 * 使用HikariCP连接池 性能好
 */
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class TestGroovyJDBCConnPoolHikariCP {
    static void main(String[] args) {
        DataSource ds = new DataSource()
        def conn = ds.getConnection()
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from test1");
        while (rs.next()){
            System.out.println(rs.getString(1) + "--" + rs.getString(2));
        }

    }
}


class DataSource{
    private static HikariConfig config = new HikariConfig()
    private static HikariDataSource dataSource

    static {
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test")
        config.setUsername("root")
        config.setPassword("123456")
        config.setDriverClassName("com.mysql.cj.jdbc.Driver")
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2480")
        dataSource = new HikariDataSource(config)
    }


    static Connection getConnection(){
        return dataSource.getConnection()
    }

    static Connection getConnection(String user, String pwd){
        return dataSource.getConnection(user, pwd)
    }
}

