package Tools;

/**
 * Description: SequenceFileInspector
 * CreateTime: 2025/8/1 14:46
 * Author Shmily
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
        Path path = new Path("/home/shmily/Downloads/Temp/part-00000-527d539c-75b0-44cc-885f-dc4f3e2cdb4d-c000");

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