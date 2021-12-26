 /**
 * HDFS文件上传
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HDFS_Download {
    public static void main(String[] args) throws IOException,URISyntaxException, InterruptedException {
        Configuration configuration = new Configuration();
        FileSystem filesystem = FileSystem.get(new URI("hdfs://hadoop101:8020"), configuration, "root");
        filesystem.copyToLocalFile(new Path("/media/xiyou.txt"),new Path("C:\\Users\\Home-PC\\Desktop\\xiyou.txt"));
        filesystem.close();
    }

}

