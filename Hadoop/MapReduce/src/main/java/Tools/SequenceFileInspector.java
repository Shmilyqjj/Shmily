package Tools;

/**
 * Description: SequenceFileInspector
 * CreateTime: 2025/8/1 14:46
 * Author Shmily
 * 运行时指定环境变量 LD_LIBRARY_PATH=/opt/modules/hadoop/lib/native
 */
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Reader;

/**
 * 检测SequenceFile 文件压缩格式
 */

public class SequenceFileInspector {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path("/home/shmily/Downloads/Temp/part-00000-e8c4734c-30d6-4062-93da-7cbb42d76de4-c000");

        try (Reader reader = new SequenceFile.Reader(fs, path, conf)) {
            System.out.println("Compression Type: " + reader.getCompressionType());
            System.out.println("Compression Codec: " +
                    (reader.getCompressionCodec() != null ?
                            reader.getCompressionCodec().getClass().getName() : "None"));
            System.out.println("Key Class: " + reader.getKeyClassName());
            System.out.println("Value Class: " + reader.getValueClassName());
        }
    }
}