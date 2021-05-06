package FlowBeanPartition;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class FlowPartitioner extends Partitioner<Text,FlowBean>{
    @Override
    public int getPartition(Text text,FlowBean flowBean,int numReduceTask) {
        //13487867673
        String phoneNum = text.toString();
        //phoneNum.substring(0,3);// 134
        int val = 0;
        if("134".equals(phoneNum.substring(0,3))){
            val = 1;
        }else if("135".equals(phoneNum.substring(0,3))){
            val = 2;
        }else if("136".equals(phoneNum.substring(0,3))){
            val = 3;
        }else if("137".equals(phoneNum.substring(0,3))){
            val = 4;
        }
        return val;
    }
}
