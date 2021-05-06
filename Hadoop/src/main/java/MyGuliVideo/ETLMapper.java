package MyGuliVideo;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


import java.io.IOException;

public class ETLMapper extends Mapper<LongWritable,Text, Text, NullWritable> {

    private Text k = new Text();

    protected void map(LongWritable key,Text value,Context context) throws IOException,InterruptedException {
        //1.获取一行数据
        String line = value.toString();
        //2.清洗数据
        String etlStr = ETLUtil.etlStr(line);
        //3.写出去
        if(StringUtils.isBlank(etlStr)){return;}
        k.set(etlStr);
        context.write(k,NullWritable.get());
    }

}
