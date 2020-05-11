package HBase.weibo;

import java.io.IOException;

public class Weibo {
    public static void init() throws IOException {
       WeiboUtil.createNameSpace("weibo");
       WeiboUtil.createTable("weibo:content",1,"info");
       WeiboUtil.createTable("weibo:users",1,"attends","fans");
       WeiboUtil.createTable("weibo:inbox",2,"info");

    }
    public static void main(String[] args) throws IOException {
        //init(); //创建完第一次后 注释掉
//        WeiboUtil.putContent("A","A1");
//        WeiboUtil.putContent("B","B1");
//        WeiboUtil.putContent("C","C1");
//        WeiboUtil.putContent("A","A2");
//        WeiboUtil.putContent("B","B2");
//        WeiboUtil.putContent("C","C2");
//        WeiboUtil.putContent("C","CNNNNNNNNN");
//          WeiboUtil.putContent("E","E1");
//          WeiboUtil.putContent("F","F1");
//        WeiboUtil.attendUser("A","B","C");
//          WeiboUtil.attendUser("A","E");
//          WeiboUtil.attendUser("A","F");
//            WeiboUtil. removeUser("A","E");
//            WeiboUtil. removeUser("A","F");
        WeiboUtil.showContent("A");

    }
}
