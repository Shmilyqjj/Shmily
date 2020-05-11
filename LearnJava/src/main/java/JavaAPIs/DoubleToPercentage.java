package JavaAPIs;

import java.text.DecimalFormat;

/**
 * double类型的小数输出百分比
 */
public class DoubleToPercentage {
    public static void main(String[] args) {
        double d = 0.52199;
        DecimalFormat df = new DecimalFormat("0.00%");
        System.out.println(df.format(d));
        DecimalFormat df1 = new DecimalFormat("0.000%");
        System.out.println(df1.format(d));
    }
}
