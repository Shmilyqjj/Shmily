import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

/**
 * :Description: path对象getFileSystem只要
 * :Author: 佳境Shmily
 * :Create Time: 2022/4/25 18:15
 * :Site: shmily-qjj.top
 */
public class HDFSPath {
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        Path path = new Path("hdfs://192.168.2.198:8020/user/hive/warehouse/232/1");
        Path path1 = new Path("hdfs://192.168.2.198:8020/user/hive/warehouse/232/232");
        Path path2 = new Path("hdfs://192.168.2.199:8020/user/hive/warehouse/232/232");
        System.out.println(path.toUri().getScheme());
        System.out.println(path.toUri().getPath());

        // true 同一schema下同一路径path子目录相同 则不会重复创建FileSystem对象 会复用之前的对象
        System.out.println(path.getFileSystem(conf) == path1.getFileSystem(conf));
        System.out.println(path.getFileSystem(conf).equals(path1.getFileSystem(conf)));
        // false 不同hdfs服务获取到不同fs对象
        System.out.println(path.getFileSystem(conf) == path2.getFileSystem(conf));
        // false uri为全路径的标记
        System.out.println(path.toUri() == path1.toUri());
        System.out.println(path.toUri());
        System.out.println(path1.toUri());
        // 是否为根目录
        System.out.println(path.isRoot());
        System.out.println(new Path("hdfs://192.168.2.198/").isRoot());
    }
}
