package order;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OrderBean implements WritableComparable<OrderBean> {
    private String orderId;
    private double price;

    public OrderBean(){ }
    public OrderBean(String orderId,double price){
        this.orderId = orderId;
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int compareTo(OrderBean o){
        int result = this.orderId.compareTo(o.orderId); //comapreTo返回值正数 0 负数  代表 大于 等于 小于
        //
        if(result == 0){
            //
            result = this.price > o.price?-1:1;
        }
        return result;
    }

    //序列化 - 从内存读入磁盘
    public void write(DataOutput out) throws IOException{
        out.writeUTF(this.orderId);
        out.writeDouble(this.price);
    }

    //逆序列化
    public void readFields(DataInput in) throws IOException{
        this.orderId = in.readUTF();
        this.price = in.readDouble();
    }

    @Override
    public String toString() {
        return orderId + "\t" + price + "\n";
    }
}
