import ru.yandex.clickhouse.BalancedClickhouseDataSource
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

class ClickHouseJDBC {
    static void main(String[] args) {
        // 连接单机CH
        connClickHouseSingle()
        // 连接分布式CH
        connClickHouseCluster()
    }


    static void connClickHouseSingle(){
        String url = "jdbc:clickhouse://cdh101:8123/default"
        ClickHouseProperties clickHouseProperties = new ClickHouseProperties()
        clickHouseProperties.setUser("default")
        clickHouseProperties.setPassword("admin")

        ClickHouseDataSource cds = new ClickHouseDataSource(url,clickHouseProperties)
        Connection conn = cds.getConnection()
        Statement stmt = conn.createStatement()
        ResultSet rs = stmt.executeQuery("select 1, hostName()")
        if (rs.next()) {
            System.out.println("res: " + rs.getInt(1) + ", " + rs.getString(2))
        }
        rs.close()
        ResultSet rs1 = stmt.executeQuery("show tables in system")
        if (rs1.next()) {
            System.out.println("res: " + rs1)
        }
        rs1.close()
    }

    static void connClickHouseCluster(){
        String url = "jdbc:clickhouse://cdh102:8123,cdh103:8123,cdh104:8123/default"
        ClickHouseProperties clickHouseProperties = new ClickHouseProperties()
        clickHouseProperties.setUser("default")
        clickHouseProperties.setPassword("admin")

        BalancedClickhouseDataSource balanced = new BalancedClickhouseDataSource(url, clickHouseProperties)
        //对每个host进行ping操作, 排除不可用的连接
        balanced.actualize()
        Connection conn = balanced.getConnection()
        Statement stmt = conn.createStatement()

        ResultSet rs = stmt.executeQuery("select 1, hostName()")
        if (rs.next()) {
            System.out.println("res: " + rs.getInt(1) + ", " + rs.getString(2))
        }
        rs.close()
        ResultSet rs1 = stmt.executeQuery("select * from system.clusters")
        if (rs1.next()) {
            System.out.println("res: " + rs1)
        }
        rs1.close()
    }

}


