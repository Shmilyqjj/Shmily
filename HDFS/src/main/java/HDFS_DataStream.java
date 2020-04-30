package main.java;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * HDFS数据流
 * 在HDFS创建文件和写入数据
 */
public class HDFS_DataStream {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        Configuration configuration = new Configuration(); //创建配置信息对象
        FileSystem fs = FileSystem.get(new URI("hdfs://192.168.1.101:9000"),configuration,"root"); //U - R - I  FileSystem是hadoop包中的

        //创建输出流
        FSDataOutputStream fsDataOutputStream = fs.create(new Path("/media/OutStream.txt"));

        //向流里写入数据
        fsDataOutputStream.write("qjj haha zxw".getBytes());  //注意：一定要是byte类型的

        //一致性刷新--让其他客户端能立刻访问（其他client立即可见）  --  如果不加，刷新慢
        fsDataOutputStream.hflush();

        //关闭输入流
        fsDataOutputStream.close();
    }
}
