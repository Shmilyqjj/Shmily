package other.problems;

import java.util.Scanner;

/**
 * :Description: 素数
 * :Author: 佳境Shmily
 * :Create Time: 2022/3/13 17:49
 * :Site: shmily-qjj.top
 * 判断101-200之间有多少个素数，并输出所有素数
 *
 * 分析：素数是指除了 1 和它本身以外，不能被任何整数整除的数，例如17就是素数，因为它不能被 2~16 的任一整数整除
 *
 * 思路1)：因此判断一个整数m是否是素数，只需把 m 被 2 ~ m-1 之间的每一个整数去除，如果都不能被整除，那么 m 就是一个素数。
 *
 * 思路2)：另外判断方法还可以简化。m 不必被 2 ~ m-1 之间的每一个整数去除，只需被 2 ~ 根号下m  之间的每一个整数去除就可以了。如果 m 不能被 2 ~ 根号下m 间任一整数整除，m 必定是素数。例如判别 17 是是否为素数，只需使 17 被 2~4 之间的每一个整数去除，由于都不能整除，可以判定 17 是素数。
 *
 * 优化： 所有质数中只有2是偶数，其他都为奇数；既为偶数又为质数的数只有2 所以只需要在101~200之间的奇数判断即可，减少遍历次数
 */
public class PrimeNumber {
    public static void main(String[] args) {
        int count = 0;
        for(int i = 101; i<= 200; i+=2){
            boolean primeFlag = true;
            for(int j = 2 ;j <= Math.sqrt(i); j++){
                if (i % j == 0){
                    primeFlag = false;
                    break;
                }
            }
            if(primeFlag){
                count++;
                System.out.println(i);
            }
        }
        System.out.println(String.format("共有%s个素数", count));
    }
}
