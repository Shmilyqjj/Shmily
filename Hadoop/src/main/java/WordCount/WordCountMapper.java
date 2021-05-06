package WordCount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class WordCountMapper extends Mapper<LongWritable,Text, Text, IntWritable> { //输入行数，输入文本，输出文本，输出个数
    protected void map(LongWritable key,Text value,Context context) throws IOException,InterruptedException{

        Text t = new Text();
        IntWritable i = new IntWritable(1);

        //1.将maptask传给我们的文本内容先转换成String   - 读取文件内容
        String line = value.toString();

        //2.根据空格将这一行切分成单词    -  按空格分割内容
        String[] words = line.split(" ");

        //3.将单词输出为<单词，1>   -   输出到缓存区
        for(String w:words){
            t.set(w);
            // 将单词作为key，将次数1作为value,以便于后续的数据分发，可以根据单词分发
            // 以便于相同单词会到相同的reducetask中
            context.write(t,i);
        }
    }
}
