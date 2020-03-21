package FlowBeanPartition;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowMapper extends Mapper<LongWritable,Text,Text,FlowBean>{
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //读文件
        Text t = new Text();

        String line = value.toString();
        //拆分
        String[] words = line.split("\t");
        // xxxxx  13888888888   xxx xxx  xxx xxxx xxxx xxx 300 666 xxx

        t.set(words[1]);
        long up = Long.parseLong(words[words.length - 3]);
        long down = Long.parseLong(words[words.length - 2]);
        FlowBean fb = new FlowBean(up,down);
        context.write(t,fb);


    }
}
