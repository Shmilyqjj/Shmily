package FlowBean;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowMapper extends Mapper<LongWritable,Text,Text,FlowBean>{  //输入键 输入值 输出键 输出值

    //map方法
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //key读入键 value读入值 context写入到缓冲区-context(a,b)

        //1.读文件
        Text t = new Text(); //Text对象 用于存手机号
        String line = value.toString();//得到数据
        //拆分数据
        String[] words = line.split("\t"); //拆分 按tab键拆分 遇到tab分割

        //test：
        //aaaa	18642196696	xxx	xxxxx	xxx	xxx	115	815	xxx

        t.set(words[1]);
        long up = Long.parseLong(words[words.length - 3]); //上行流量 长度-3是下标 Long.parseLong强制类型转换
        long down = Long.parseLong(words[words.length - 2]);
        FlowBean fb = new FlowBean(up,down); //封装up和down的信息 为 Bean对象

        //写入到缓冲区
        context.write(t,fb);
    }
}
