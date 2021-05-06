package order;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class OrderGroupingComparator extends WritableComparator {
    public OrderGroupingComparator(){
        super(OrderBean.class,true);
    }

    public int compare(WritableComparable a, WritableComparable b) {
        OrderBean ob1 = (OrderBean)a;
        OrderBean ob2 = (OrderBean)b;

        return ob1.getOrderId().compareTo(ob2.getOrderId());
    }
}
