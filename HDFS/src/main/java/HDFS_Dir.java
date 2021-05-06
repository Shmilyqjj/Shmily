

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class HDFS_Dir {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        Configuration configuration = new Configuration();
        FileSystem fs = FileSystem.get(new URI("hdfs://cdh101:8020"), configuration, "root");

        //1.HDFS目录创建
        //fs.mkdirs(new Path("hadoop101:8020/media/input"));  //在hadoop101 创建文件夹  前面没定义就要加上hdfs://hadoop101:8020
        //fs.mkdirs(new Path("/media/input"));

        //2.HDFS文件夹删除
//        fs.delete(new Path("/media/input"),true);

        //3.HDFS文件名修改
//        fs.rename(new Path("hdfs://hadoop101:8020/media/xiyou.txt"),new Path("hdfs://hadoop101:8020/media/XIYOU.txt"));

        //4.HDFS文件详情查看   只能看文件  看不到文件夹
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/user/hive/warehouse/test"), true);
        while (listFiles.hasNext()) {
            LocatedFileStatus fileStatus = listFiles.next();
            System.out.println("*************" + fileStatus.getPath().getName() + "******************");
            System.out.println(fileStatus.getBlockSize());
            System.out.println(fileStatus.getPermission());
            System.out.println(fileStatus.getLen());
            System.out.println("修改时间:" + fileStatus.getModificationTime());
            BlockLocation[] blockLocations = fileStatus.getBlockLocations();

            for (BlockLocation bl : blockLocations) {

                System.out.println("block-offset:" + bl.getOffset());

                String[] hosts = bl.getHosts();

                for (String host : hosts) {
                    System.out.println(host);
                }
            }
            System.out.println("************************************************************");
        }
    }

}
