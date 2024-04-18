package seq;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class ReadSequenceFile {
    public static void main(String[] args) {
        // 文件路径（替换为您的 GZIP 压缩的 SequenceFile 文件路径）
        String path = "/home/shmily/Downloads/Temp/odl_game_heartbeat_d_20240417.seq";
//        String path = "/home/shmily/Downloads/Temp/odl_yunpan_mongodb_sladmin_yunpanpartner_20240417.seq";
        Path sequenceFilePath = new Path(path);

        // 创建一个 Hadoop 配置对象
        Configuration conf = new Configuration();

        // 创建 SequenceFile.Reader
        try (SequenceFile.Reader reader = new SequenceFile.Reader(
                conf,
                SequenceFile.Reader.file(sequenceFilePath)
        )) {
            // 定义键值对
            NullWritable key = NullWritable.get();
            Text value = new Text();

            // 读取数据
            while (reader.next(key, value)) {
                // 打印键值对
                System.out.println("Key: " + key + ", Value: " + value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
