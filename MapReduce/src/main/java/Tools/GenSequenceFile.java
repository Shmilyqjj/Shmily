package Tools;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.net.URI;

/**
 * @Descriptioo: 生成Hadoop Sequence文件
 * @Owner: jiajing_qu
 * @Date: 2019/12/8 23:21
 * @Version: 1.0
 */

public class GenSequenceFile {
    private static String[] myvalues = {
            "hello world",
            "qjj aaa",
            "hello hadoop",
            "qjj bbb"
    };

    public static void main(String args[]) throws IOException {
        String url = "hdfs://localhost:9000/datas/spark_test/outputs/sequence_files";
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(url), conf);
        org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(url);

        IntWritable key = new IntWritable();
        Text value = new Text();

        SequenceFile.Writer writer = null;

        try{
            writer = SequenceFile.createWriter(fs, conf, path, key.getClass(), value.getClass());

            for(int i = 0; i < 100; i++){
                key.set(i);
                value.set(myvalues[i%myvalues.length]);

                writer.append(key, value);
            }
        }
        finally{
            IOUtils.closeStream(writer);
        }
    }
}
