package WordCount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class WordCountReducer extends Reducer<Text,IntWritable,Text,IntWritable>{
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        // 1.汇总各个key的个数
        for (IntWritable i:values) {
            count += i.get();
        }

       //  2.输出该key的总次数
        context.write(key,new IntWritable(count));
        // hello  5
        // word 3
        // weichuang 2
    }
}
