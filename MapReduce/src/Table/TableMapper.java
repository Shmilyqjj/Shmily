package Table;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class TableMapper extends Mapper<LongWritable,Text,Text,TableBean>{
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        FileSplit inputSplit = (FileSplit) context.getInputSplit();
        String fileName = inputSplit.getPath().getName();//文件名称

        String line = value.toString();
        TableBean tb = new TableBean();
        if(fileName.startsWith("order")){
            String[] words = line.split("\t");
            // 1001 01 1
            tb.setOrderId(words[0]);
            tb.setpId(words[1]);
            tb.setAmount(Integer.parseInt(words[2]));
            tb.setpName("");
            tb.setFlag("0");
        }else{
            String[] words = line.split("\t");
            // 01 小米
            tb.setpId(words[0]);
            tb.setpName(words[1]);
            tb.setOrderId("");
            tb.setAmount(0);
            tb.setFlag("1");
        }

        context.write(new Text(tb.getpId()),tb);


    }
}
