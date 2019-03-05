/**
 * 对大的数据进行计算 对更大精度的小数进行计算  对数据快速格式化
 * 对超过精度的数据进行操作和运算的API
 * date:2019.3.5
 */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Arrays;

public class BigDataAPI {
    public static void main(String[] args) {
        String i1 = "54657947136526646532";
        String i2 = "51156462542562554698";
        BigInteger i3 = new BigInteger(i1);
        BigInteger i4 = new BigInteger(i2);

        System.out.println(i3.add(i4)); //加法 -不存在越界问题
        System.out.println(i3.subtract(i4)); //减法
        System.out.println(i3.multiply(i4)); //乘法
        System.out.println(i3.divide(i4)); //除法取整
        System.out.println(i3.remainder(i4)); //除法取余数
        System.out.println(Arrays.toString(i3.divideAndRemainder(i4))); //除法取整+取余 array数组类型


        System.out.println("---------------------------------------------------");

        //BigDecimal
        String i5 = "123.512345647995792453250255";
        String i6 = "230.512345647995798514325255";
        BigDecimal b1 = new BigDecimal(i5);
        BigDecimal b2 = new BigDecimal(i6);
        System.out.println(b1.add(b2));
        System.out.println(b1.subtract(b2));
        System.out.println(b1.multiply(b2));
        System.out.println(b1.divide(b2,2,BigDecimal.ROUND_HALF_UP)); //除法，保留两位，四舍五入

        System.out.println("---------------------------------------------------");

        //DecimalFormat类-用最快的速度将数字格式化为需要的样子
        double pi = 3.1415927;
        System.out.println(new DecimalFormat("0").format(pi));
        System.out.println(new DecimalFormat("0.0").format(pi));
        System.out.println(new DecimalFormat("0.00").format(pi));
        System.out.println(new DecimalFormat("00.000").format(pi));
        System.out.println(new DecimalFormat("#").format(pi));//取所有整数部分
        System.out.println(new DecimalFormat("#.##%").format(pi));//以百分比方式计数，并取两位小数


    }
}
