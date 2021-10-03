import groovy.sql.Sql

class TestHiveConn {
    static void main(String[] args) {
        hiveConn("d_bd")
    }

    static void hiveConn(String tableName){
        def test_hive_url="jdbc:hive2://thriftserver_or_hiveserver_ip:port/default"
        def test_hive_user="user"
        def test_hive_password="pwd"
        def test_conn = Sql.newInstance(test_hive_url, test_hive_user, test_hive_password, "org.apache.hive.jdbc.HiveDriver")
        def l = []
        test_conn.eachRow(String.format("show tables in %s", tableName)) {
            l.add(it.getString(2))
        }
        println(l)
    }
}


