import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * :Description: 查看HDFS目录更新时间
 * :Author: 佳境Shmily
 * :Create Time: 2020/9/13 22:57
 * :Site: shmily-qjj.top
 */
public class HDFSPathModifactionTime {
    public static void main(String[] args) throws IOException {

        Configuration conf = new Configuration();
        conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
        conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
        FileSystem fs = FileSystem.get(conf);
        System.out.println("输入目录名称:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Path path = new Path(br.readLine());
        displayDirectoryContents(fs, path);
        fs.close();
    }

    private static void displayDirectoryContents(FileSystem fs, Path rootDir) {
        try {
            FileStatus[] status = fs.listStatus(rootDir);
            for (FileStatus file : status) {
                if (file.isDirectory()) {
                    System.out.println("DIRECTORY:" + file.getPath() + " - 上次修改time:" + file.getModificationTime());
                    displayDirectoryContents(fs, file.getPath());
                } else {
                    System.out.println("FILE:" + file.getPath() + " - 最后修改时间:" + file.getModificationTime());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}