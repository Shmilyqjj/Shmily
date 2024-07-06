package FlowBean;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FlowBean implements Writable {
    private long UpFlow;  //上行流量
    private long DownFlow;  //下行流量
    private long TotalFlow;    //总流量

    public FlowBean(long UpFlow,long DownFlow){   //构造方法
        this.UpFlow = UpFlow;
        this.DownFlow = DownFlow;
        this.TotalFlow = UpFlow+DownFlow;
    }
    public FlowBean(){

    }

    //-从内存读到硬盘-序列化
    public void write(DataOutput out) throws IOException {
        out.writeLong(this.UpFlow);
        out.writeLong(this.DownFlow);
        out.writeLong(this.TotalFlow);
    }


    //-从硬盘读到内存-逆序列化
    public void readFields(DataInput in) throws IOException {
        this.UpFlow = in.readLong();
        this.DownFlow = in.readLong();
        this.TotalFlow = in.readLong();
    }

    public long getUpFlow() {
        return UpFlow;
    }

    public void setUpFlow(long upFlow) {
        UpFlow = upFlow;
    }

    public long getDownLoadFlow() {
        return DownFlow;
    }

    public void setDownLoadFlow(long downLoadFlow) {
        DownFlow = downLoadFlow;
    }

    public long getTotalFlow() {
        return TotalFlow;
    }

    public void setTotalFlow(long totalFlow) {
        TotalFlow = totalFlow;
    }

    @Override   //toString方法 输出 - 实例化FlowBean时自动调用
    public String toString() {
        return UpFlow + "\t"+ DownFlow + "\t"+ TotalFlow +"\n";
    }
}
