import alluxio.AlluxioURI;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileOutStream;
import alluxio.exception.AlluxioException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.IOException;

/**
 * HDFS & Allxuio IO读取文件测试工具  IO接口 文件API
 */

public class IOTestUtil {
    public static void main(String[] args) throws IOException, AlluxioException {
        String filePath = args[0];
        HDFSUtil h = new HDFSUtil("hdfs://192.168.1.101:8020");
        h.readFile(filePath);
        AlluxioUtil a = new AlluxioUtil();
        a.readFile(filePath);
        System.out.println("读文件测试 Finished");
        System.out.println("------------------------");
        if (args.length != 1){
            String fileToWritePath = args[1];
            a.writeFile(fileToWritePath);
            System.out.println("写文件测试 Finished");
        }
    }
}

class HDFSUtil{
    private Configuration conf = new Configuration();
    public HDFSUtil(String HDFSURL){
        conf.set("fs.defaultFS",HDFSURL);
        System.setProperty("HADOOP_USER_NAME","hdfs");
    }
    public void readFile(String path) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        fs.getFileStatus(new Path(path));
        FSDataInputStream in = fs.open(new Path(path));
        try{
            long hdfsStartTime=System.currentTimeMillis();
            in = fs.open(new Path(path));
            byte[] buffer = new byte[1024];
            int byteRead = 0;
            while ((byteRead = in.read(buffer)) != -1) {
                System.out.write(buffer, 0, byteRead);    //输出字符流
            }
            long hdfsEndTime=System.currentTimeMillis();
            System.out.println("HDFS读取运行时间:"+(hdfsEndTime-hdfsStartTime)+" ms");
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            in.close();
        }
    }
}

class AlluxioUtil{
    private static final alluxio.client.file.FileSystem fs = alluxio.client.file.FileSystem.Factory.get();
    public AlluxioUtil(){}
    public FileInStream readFile(String AlluxioPath) throws IOException, AlluxioException {
        AlluxioURI path = new AlluxioURI(AlluxioPath);  //封装Alluxio 文件路径的path
        FileInStream in = fs.openFile(path);
        try{
            long startTime=System.currentTimeMillis();
            in = fs.openFile(path);
            // 调用文件输入流FileInStream实例的read()方法读数据
            byte[] buffer = new byte[1024];
            int byteRead = 0;
            // 读入多个字节到字节数组中，byteRead为一次读入的字节数
            while ((byteRead = in.read(buffer)) != -1) {
                System.out.write(buffer, 0, byteRead);    //输出字符流
            }
            long endTime=System.currentTimeMillis();
            System.out.println("Alluxio读取运行时间:"+(endTime-startTime)+" ms");
        }catch (IOException | AlluxioException e){
            e.printStackTrace();
        }finally {
            in.close();
        }
        in.close();  //关闭文件并释放锁
        return in;
    }
    public void writeFile(String AlluxioPath) throws IOException, AlluxioException {
        AlluxioURI path = new AlluxioURI(AlluxioPath);   // 文件夹路径
        FileOutStream out = null;
        try {
            out = fs.createFile(path);   //创建文件并得到文件输入流
            out.write("qjj1234567".getBytes());   // 调用文件输出流FileOutStream实例的write()方法写入数据
        }catch (IOException | AlluxioException e){
            e.printStackTrace();
        }finally {
            out.close();  // 关闭和释放文件
        }
    }
}