package basic.grammar
import groovy.sql.Sql
import org.apache.commons.dbcp.BasicDataSource

class TestGroovyJDBCConnPool {
    static void main(String[] args) {
        String dbName  = "test"
        String user  = "root"
        String pwd  = "123456"

        // 使用dbcp连接池连接
        def datasource = new BasicDataSource(driverClassName: "com.mysql.jdbc.Driver",
                url: "jdbc:mysql://localhost:3306/${dbName}", username: user, password: pwd)
        def sql = new Sql(datasource)

        sql.eachRow("show tables"){
            print(it)
        }

        def sql1 = new Sql(datasource)
        sql1.eachRow("select *  from test1"){
            print(it)
        }

        datasource.close()


    }
}
