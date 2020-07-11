package top.qjj.shmily.operations;

import org.apache.kudu.client.SessionConfiguration.FlushMode;
import org.apache.kudu.client.*;

/**
 * :Description: Kudu DML 数据操作 Insert Delete Update ===CRUD===
 * :Author: 佳境Shmily
 * :Create Time: 2020/7/11 20:23
 * :Site: shmily-qjj.top
 */
public class KuduDML {
    public static void main(String[] args) {
        String masterAddr = "cdh102,cdh103,cdh104";
        KuduDMLOperations kuduDMLOperations = KuduDMLOperations.getInstance(masterAddr);

        //插入数据
        kuduDMLOperations.insertRows();

        //更新一条数据
        kuduDMLOperations.updateRow();

        //删除一条数据
        kuduDMLOperations.deleteRow();

        //查询数据
        kuduDMLOperations.selectRows();

        //关闭Client连接
        kuduDMLOperations.closeConnection();
    }
}


class KuduDMLOperations {
    private static volatile KuduDMLOperations instance;
    private KuduClient kuduClient = null;
    private KuduDMLOperations(String masterAddr){
        kuduClient = new KuduClient.KuduClientBuilder(masterAddr).defaultOperationTimeoutMs(6000).build();
    }
    public static KuduDMLOperations getInstance(String masterAddr){
        if(instance == null){
            synchronized (KuduDMLOperations.class){
                if(instance == null){
                    instance = new KuduDMLOperations(masterAddr);
                }
            }
        }
        return instance;
    }

    public void closeConnection() {
        try {
            kuduClient.close();
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    /**
     * 以kudu_table_with_hash表(id INT,name STRING)为例 插入数据
     * 注意：写数据时数据不支持为null，需要对进来的数据判空
     */
    public void insertRows(){
        try {
            KuduTable table = kuduClient.openTable("kudu_table_with_hash");  //打开表
            KuduSession kuduSession = kuduClient.newSession();  //创建会话Session
            kuduSession.setFlushMode(FlushMode.MANUAL_FLUSH);  //设置数据提交方式
            /**
             * 1.AUTO_FLUSH_SYNC（默认） 目前比较慢
             * 2.AUTO_FLUSH_BACKGROUND  目前有BUG
             * 3.MANUAL_FLUSH  目前效率最高 远远高于其他
             * 关于这三个参数测试调优可看这篇：https://www.cnblogs.com/harrychinese/p/kudu_java_api.html
             */
            int numOps = 3000;
            kuduSession.setMutationBufferSpace(numOps); //设置MANUAL_FLUSH需要设置缓冲区操作次数限制 如果超限会抛异常
            int nowOps = 0;  //记录当前操作数
            for(int i = 0; i <= 100; i++){
                Insert insert = table.newInsert();
                //字段数据
                insert.getRow().addInt("id", i);
                insert.getRow().addString("name", "小"+i);
                nowOps += 1;
                if(nowOps == numOps / 2){ //所以缓冲区操作次数达到一半时进行flush提交数据，避免抛异常
                    kuduSession.flush();  //提交数据
                    nowOps = 0;  //计数器归零
                }
                kuduSession.apply(insert);
            }
            kuduSession.flush();  //保证最后都提交上去了
            kuduSession.close();
            System.out.println("数据成功写入Kudu表");
        } catch (KuduException e) {
            e.printStackTrace();
            System.out.println("数据写入失败，原因：" + e.getMessage());
        }
    }

    /**
     * 以kudu_table_with_hash表(id INT,name STRING)为例 查询数据
     */
    public void selectRows(){
        try {
            KuduTable table = kuduClient.openTable("kudu_table_with_hash"); // 打开表
            KuduScanner scanner = kuduClient.newScannerBuilder(table).build();  //创建Scanner
            while (scanner.hasMoreRows()){
                for (RowResult r: scanner.nextRows()) {
                    System.out.println(r.getInt("id") + " - " + r.getString(1));
                }
            }
            scanner.close();
        } catch (KuduException e) {
            e.printStackTrace();
            System.out.println("查询失败，原因：" + e.getMessage());
        }
    }

    /**
     * 以kudu_table_with_hash表(id INT,name STRING)为例 更新一条数据
     */
    public void updateRow(){
        try {
            KuduTable table = kuduClient.openTable("kudu_table_with_hash");
            KuduSession session = kuduClient.newSession();
            session.setFlushMode(FlushMode.AUTO_FLUSH_SYNC);
            Update update = table.newUpdate();
            PartialRow row = update.getRow();
            row.addInt("id", 66);
            row.addString("name", "qjj");
            session.apply(update);
            session.close();
        } catch (KuduException e) {
            e.printStackTrace();
            System.out.println("数据更新失败，原因：" + e.getMessage());
        }
    }

    /**
     * 以kudu_table_with_hash表(id INT,name STRING)为例 删除一条数据
     */
    public void deleteRow(){
        try {
            KuduTable table = kuduClient.openTable("kudu_table_with_hash");
            KuduSession session = kuduClient.newSession();
            Delete delete = table.newDelete();
            delete.getRow().addInt("id",18);  //根据主键唯一删除一条记录
            session.flush();
            session.apply(delete);
            session.close();
        } catch (KuduException e) {
            e.printStackTrace();
            System.out.println("数据删除失败，原因：" + e.getMessage());
        }
    }
}