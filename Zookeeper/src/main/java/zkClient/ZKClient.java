/**
 * 类名首字母大写
 * 方法名首字母小写
 */
package zkClient;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.apache.zookeeper.ZooDefs.OpCode.exists;

public class ZKClient {
    private ZooKeeper zk = null;

    public ZKClient() throws KeeperException, InterruptedException {
    }

    //0.连接zookeeper
    @Before
    public void connect() throws IOException {
        String connect = "hadoop101:2181,hadoop102:2181,hadoop103:2181"; // 创建链接
        int sessionTime = 2000; // 2s

        zk = new ZooKeeper(connect, sessionTime, new Watcher() {
            @Override
            public void process(WatchedEvent event) {

            }
        });
    }

    //1.创建节点的API
    @Test
    public void createNode() throws KeeperException, InterruptedException {
        String path = zk.create("/shmily/qjj","qjjqjj".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);//CreateMode.PERSISTENT是创建永久节点 E开头是创建暂时节点 带S是带序列号节点  Ids.就能提示第三个参数的整体
        System.out.println(path); // 输出创建的节点的路径
    }

    //2.判断节点是否存在
    @Test
    public void exist() throws KeeperException, InterruptedException {
        Stat exists = zk.exists("/shmily",true); //true是代表显示watch监听的信息  false不显示
        if(exists == null){
            System.out.println("not exists");
        }else{
            System.out.println("exists");
        }
    }

    //3.得到节点数据 getChildren()方法
    @Test
    public void getChildren() throws KeeperException, InterruptedException {
//        List<String> children = zk.getChildren("/shmily",true);
//        for (String s:children) {
//            System.out.println(s);
//        }
//        Thread.sleep(Long.MAX_VALUE); //让程序睡眠，不结束
//    }

    List<String> children = zk.getChildren("/shmily", new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            System.out.println("aaaaaaaaaaaaaaaa");
            System.out.println("-------------------");
            try {
                getChildren();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
        for (String s:children) {
        System.out.println(s);
    }
        Thread.sleep(Long.MAX_VALUE);
}



}

