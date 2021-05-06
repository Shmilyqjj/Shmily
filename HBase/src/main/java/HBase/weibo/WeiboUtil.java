package HBase.weibo;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeiboUtil {
    private static Configuration conf = null;
    private static Connection connection = null;
    private static HBaseAdmin admin = null;
    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "192.168.1.101");
        try {
            connection = ConnectionFactory.createConnection(conf);
            admin = (HBaseAdmin)connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//创建命名空间
    public static void createNameSpace(String ns) throws IOException {
        NamespaceDescriptor nsd = NamespaceDescriptor.create(ns).build();
        admin.createNamespace(nsd);
    }

//创建表
    public static void createTable(String tableName,int version,String... cfs) throws IOException {
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(tableName));
        for (String cf : cfs) {
            HColumnDescriptor hcd = new HColumnDescriptor(cf);
            hcd.setMaxVersions(version);
            htd.addFamily(hcd);
        }
        admin.createTable(htd);

    }

//创建微博内容
    public static void putContent(String uid,String val)throws IOException{  //需要userid和微博内容val
        Table conTable = connection.getTable(TableName.valueOf("weibo:content")); //微博内容表
        Table userTable = connection.getTable(TableName.valueOf("weibo:users"));//微博用户表
        Table inTable = connection.getTable(TableName.valueOf("weibo:inbox"));//微博消息表 inbox

        //1.将内容插入到content表
        long ts = System.currentTimeMillis(); //时间戳，获取当前系统时间  rowkey是uid_ts
        String rowkey = uid + "_" + ts; //long可以直接转型String
        Put p = new Put(rowkey.getBytes()); //p对象的rowkey
        p.addColumn("info".getBytes(),"content".getBytes(),val.getBytes()); //添加每行的信息
        conTable.put(p);//插入到conTable

        //2.从users表找到uid的fans
        Get g = new Get(uid.getBytes());//得到uid
        g.addFamily("fans".getBytes()); //限制得到fans列族

        Result result = userTable.get(g);//获取粉丝数据，得到所有粉丝的列
        Cell[] cells = result.rawCells();//得到粉丝数据中每行的信息
        if(cells.length == 0){
            return; //若无粉丝，返回
        }

        List<Put> p1 = new ArrayList<>();//Put类型数组
        for (Cell cell : cells) {
            byte[] bytes = CellUtil.cloneQualifier(cell); //将获得到的每行粉丝信息的字段（qualifier）即cell存入byte数组

        //3.根据fans在inbox中加入数据
            Put inboxP = new Put(bytes);//插入信息bytes到inboxP 先建立inboxP对象
            inboxP.addColumn("info".getBytes(),uid.getBytes(),rowkey.getBytes()); //向inboxP插入其他信息
            p1.add(inboxP); //将每行信息添加到Put数组-p1
        }
        inTable.put(p1);//将p1插入inTable
    }

//添加关注
public static void attendUser(String uid,String... attender) throws IOException {
    Table conTable = connection.getTable(TableName.valueOf("weibo:content"));
    Table userTable = connection.getTable(TableName.valueOf("weibo:users"));
    Table inTable = connection.getTable(TableName.valueOf("weibo:inbox"));
    //1.在user里面添加attends
    Put p = new Put(uid.getBytes());
    List<Put> userL = new ArrayList<>();
    for (String s : attender) {//B C
        p.addColumn("attends".getBytes(),s.getBytes(),s.getBytes());
     //2 在user里面添加fans
        Put p1 = new Put(s.getBytes());
        p1.addColumn("fans".getBytes(),uid.getBytes(),uid.getBytes());
        userL.add(p1);
    }
    userL.add(p);
    userTable.put(userL);

    //3.在inbox添加数据
    Put inP = new Put(uid.getBytes());
    for (String s : attender) {
        Scan scan = new Scan(s.getBytes(),(s+"|").getBytes()); //"|"是最大的，所以s+"|"是结尾
        ResultScanner scanner = conTable.getScanner(scan);
        for (Result result : scanner) {
            byte[] rowkey = result.getRow();//rowkey
            inP.addColumn("info".getBytes(),s.getBytes(),rowkey);
        }
    }
    inTable.put(inP);
}

//移除关注用户-取关
    public static  void  removeUser(String uid,String user) throws IOException {
        Table userTable = connection.getTable(TableName.valueOf("weibo:users"));
        Table inTable = connection.getTable(TableName.valueOf("weibo:inbox"));
        //1.移除user uid行attends列族user列
        Delete del = new Delete(uid.getBytes());
        List<Delete> dList = new ArrayList<>();
        del.addColumn("attends".getBytes(),user.getBytes());
        dList.add(del);

        //2.移除user user行fans列族uid列
        Delete del1 = new Delete(user.getBytes());
        del1.addColumn("fans".getBytes(),uid.getBytes());
        dList.add(del1);

        userTable.delete(dList);

        //3.移除inbox uid行user列
        Delete del2 = new Delete(uid.getBytes());
        del2.addColumn("info".getBytes(),user.getBytes());
        inTable.delete(del2);
    }

//获取关注的人的微博
    public static  void showContent(String uid) throws IOException {
        Table conTable = connection.getTable(TableName.valueOf("weibo:content"));
        Table inTable = connection.getTable(TableName.valueOf("weibo:inbox"));
        //获取uid对应的uid+t的值去寻找content
        Get g = new Get(uid.getBytes());
        g.setMaxVersions(); //使用原表自带的版本号
        Result result = inTable.get(g);
        Cell[] cells = result.rawCells();
        List<Get> gList = new ArrayList<>();
        for (Cell cell : cells) {
            byte[] bytes = CellUtil.cloneValue(cell);
            Get conGet  = new Get(bytes);
            gList.add(conGet);
        }
        //得到微博数据
        Result[] results = conTable.get(gList);
        for (Result result1 : results) {
            Cell[] cells1 = result1.rawCells();
            for (Cell cell : cells) {
                System.out.println("content:"+ Bytes.toString(CellUtil.cloneValue(cell)));
            }

        }
    }







}
