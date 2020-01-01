import java.util.Scanner;

/**
 * 贝壳找房笔试在线编程题 02
 *题目描述：
 * 小希偶然得到了传说中的月光宝盒,然而打开月光宝盒需要一串密码。虽然小希并不知道密码具体是什么，但是月光宝盒的说明书上有着一个长度为 n (2 <= N <= 50000)的序列 a (-10^9 <= a[i] <= 10^9)的范围内。下面写着一段话：密码是这个序列的最长的严格上升子序列的长度(严格上升子序列是指，子序列的元素是严格递增的，例如: [5,1,6,2,4]的最长严格上升子序列为[1,2,4])，请你帮小希找到这个密码。
 *
 *
 * 输入
 * 第1行：1个数N，N为序列的长度(2<=N<=50000)
 *
 * 第2到 N+1行：每行1个数，对应序列的元素(-10^9 <= a[i] <= 10^9)
 *
 * 输出
 * 一个正整数表示严格最长上升子序列的长度
 *
 *
 * 样例输入
 * 8
 * 5
 * 1
 * 6
 * 8
 * 2
 * 4
 * 5
 * 10
 * 样例输出
 * 5
 */

public class Solution04 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = in.nextInt();
        }

        int[] old = values;
        sort(values);

        int temp = values[1];
        int count = 0;
        for (int i = 0; i < old.length-1; i++) {
            if(old[i] > temp && old[i+1]>temp){
                count++;
            }
        }
        System.out.println(count);
    }


    public static void sort(int[] a){
        int temp;
        for (int i = 0; i < a.length-1; i++) {
            for (int j = 0; j < a.length-1-i; j++) {
                if(a[j+1]<a[j]){
                    temp = a[j];
                    a[j] = a[j+1] ;
                    a[j+1] = temp;

                }
            }
        }
    }

}
