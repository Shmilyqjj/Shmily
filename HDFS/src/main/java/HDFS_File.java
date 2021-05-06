import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;

public class HDFS_File{
    public static void main(String[] args) throws Exception, IllegalArgumentException, IOException  {
        // 1 创建配置信息对象
        Configuration configuration = new Configuration();

        FileSystem fs = FileSystem.get(new URI("hdfs://cdh101:8020"),configuration, "root");

        // 2 获取查询路径下的文件状态信息
        FileStatus[] listStatus = fs.listStatus(new Path("/"));

        // 3 遍历所有文件状态
        for (FileStatus status : listStatus) {
            if (status.isFile()) {
                System.out.println("f--" + status.getPath().getName());
            } else {
                System.out.println("d--" + status.getPath().getName());
            }
        }
    }
}


