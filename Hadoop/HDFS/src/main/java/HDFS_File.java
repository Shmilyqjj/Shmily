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
        Common.setKerberos(configuration, "/etc/krb5.conf", "/opt/keytabs/hdfs.keytab", "hdfs");
        configuration.addResource(new Path("/etc/hadoop-conf/core-site.xml"));
        configuration.addResource(new Path("/etc/hadoop-conf/hdfs-site.xml"));

        FileSystem fs = FileSystem.get(configuration);

        // 2 获取查询路径下的文件状态信息
        FileStatus[] listStatus = fs.listStatus(new Path("/tmp/logs/iceberg_hive.err"));

        // 3 遍历所有文件状态
        for (FileStatus status : listStatus) {
            if (status.isFile()) {
                // 获取文件最后访问Time
                System.out.println(status.getPath() + " 文件AccessTime: " + Common.getHumanReadableTime(status.getAccessTime()));
            } else {
                System.out.println(status.getAccessTime());
                System.out.println("d--" + status.getPath().getName());
            }
        }
    }
}


