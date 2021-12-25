package basic.grammar
import groovy.sql.Sql

import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

/**
 * 测试Impala jdbc连接权限及可用性 测试jdbc jar兼容性
 *  groovy -cp /hadoop/bigdata/common/lib/ImpalaJDBC41.jar ~/qjj/TestImpalaConn.groovy
 */

class TestImpalaConn {
    static void main(String[] args) {
//        def test_conn = Sql.newInstance("jdbc:impala://impalad_ip:21050/ukudu", "user", "pwd", "com.cloudera.impala.jdbc41.Driver")
//        test_conn.eachRow(String.format("select * from k_tag_sb limit 10")) {
//            println(it)
//        }

        Class.forName("com.cloudera.impala.jdbc41.Driver")
        def conn = DriverManager.getConnection("jdbc:impala://impalad_ip:21050/ukudu;AuthMech=3;UID=user;PWD=pwd")

//        ResultSet resultSet = conn.prepareStatement("select * from table_name limit 10").executeQuery();
//        while (resultSet.next()) {
//            println(resultSet.getString(1) + "" + resultSet.getString(0))
//        }


        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from table_name limit 10");
        System.out.println("---begin query---");

        //打印输出
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }

    }
}


