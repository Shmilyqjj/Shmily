import java.math.BigInteger;
import java.util.Scanner;

/**
 * 网易笔试第一题：求超大超大的两个数的最大公约数，大数超过基本数据类型的取值范围
 * 例子：
 * 6 4
 * 2
 * .....
 *565661566666 894454515488
 *2
 */

public class Solution01 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String a = sc.next();
        String b = sc.next();
        BigInteger Ba = new BigInteger(a);
        BigInteger Bb = new BigInteger(b);
        System.out.println(GYS(Ba, Bb).toString());
    }
    public static BigInteger GYS(BigInteger x,BigInteger y){
        if(x.compareTo(y) == 1){
            BigInteger temp = x;
            x = y;
            y = temp;
        }
        while(x.compareTo(new BigInteger("0")) != 0){
            BigInteger temp = y.mod(x);
            y = x;
            x = temp;
        }
        return y;
    }
    }
