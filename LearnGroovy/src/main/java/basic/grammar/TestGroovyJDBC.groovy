package basic.grammar
import groovy.sql.Sql

class TestGroovyJDBC {
    static void main(String[] args) {
        String dbName  = "test"
        String user  = "root"
        String pwd  = "123456"
        def sql = Sql.newInstance("jdbc:mysql://localhost:3306/${dbName}", user, pwd, "com.mysql.jdbc.Driver")

        // 读
        sql.eachRow("select * from test1"){
            println(it)
        }
        sql.eachRow("select * from test1"){
            println(it.id + " -- " + it.name)
        }

        // 执行
        // 事物提交与会滚
        sql.connection.setAutoCommit(false)  // 关闭autocommit 才能调用回滚
        try {
            sql.execute("insert into test1 values (999, 'haha')")
            sql.commit()
            println("Successfully committed")
        }catch(Exception ex) {
            sql.rollback()
            println("Transaction rollback")
        }


        sql.close()



    }
}
