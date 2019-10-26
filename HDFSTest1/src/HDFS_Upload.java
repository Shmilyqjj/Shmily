/**
 * HDFS文件上传 - 本地上传到HDFS
 */

import org.apache.commons.httpclient.URIException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HDFS_Upload {
    public static void main(String[] args) throws IOException, URIException, URISyntaxException, InterruptedException {
        Configuration configuration = new Configuration(); //获取文件系统
        FileSystem filesystem = FileSystem.get(new URI("hdfs://hadoop101:9000"), configuration, "root"); //配置在集群上运行   //core-site.xml中<name>fs.defaultFS</name>   <value>hdfs://hadoop:9000</value>  代表端口9000


        filesystem.copyFromLocalFile(new Path("d://test.txt"),new Path("/media/test.txt"));//FileSystem类实例化后调用copyFromLocalFile(Path,Path)方法 path对象里填写本地文件和HDFS文件的路径
        filesystem.close();//关闭filesystem
        }

    }

