package qjj.alluxio.api.test;
import alluxio.AlluxioURI;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileOutStream;
import alluxio.client.file.FileSystem;
import alluxio.exception.AlluxioException;

import java.io.IOException;

/**
 * Alluxio分布式存储系统的API操作工具
 */
public class AlluxioIO {
    public static void main(String[] args) throws IOException, AlluxioException {
        String path = args[0];
        if(path == null);
            path = "/media/video/2008/0222/4.txt";
        AlluxioUtil au = new AlluxioUtil();
        au.readFile(path);

    }
}

//Alluxio工具类
class AlluxioUtil{
    private static final FileSystem fs = FileSystem.Factory.get();
    public AlluxioUtil(){}
    public FileInStream readFile(String AlluxioPath) throws IOException, AlluxioException {
        AlluxioURI path = new AlluxioURI(AlluxioPath);
        FileInStream in = fs.openFile(path);
        long startTime=System.currentTimeMillis();
        int read = in.read();//读取数据
        long endTime=System.currentTimeMillis();
        System.out.println(read);
        System.out.println("Alluxio读取运行时间:"+(endTime-startTime)+" ms");
        in.close();  //关闭文件并释放锁
        return in;
    }

    public FileOutStream writeFile(String AlluxioPath) throws IOException, AlluxioException {
        AlluxioURI path = new AlluxioURI(AlluxioPath);
        // Create a file and get its output stream
        FileOutStream out = fs.createFile(path);
        // Write data
        out.write(null);
        // Close and complete file
        out.close();
        return out;
    }
}
