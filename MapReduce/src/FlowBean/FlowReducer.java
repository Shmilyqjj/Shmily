package FlowBean;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FlowReducer extends Reducer<Text,FlowBean,Text,FlowBean> {
    //Text是由map输入的键
    //FlowBean是由map输入的值
    //Text是由Reduce输出的键
    //FlowBean是由Reduce输出的值

    //reduce方法
    protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {
        //数据定义和初始化
        long up = 0;
        long down = 0;

        //统计总流量
        for(FlowBean fb:values){
            up += fb.getUpFlow();
            down += fb.getDownLoadFlow();
        }

        context.write(key,new FlowBean(up,down));
    }




}
