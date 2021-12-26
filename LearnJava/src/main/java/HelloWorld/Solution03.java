package HelloWorld;

import java.util.Scanner;

import static java.lang.Math.abs;

/**
 * 贝壳找房笔试在线编程题 01
 * Main
 * 2019.8.10
 *
 * 题目描述：
 * 给出n个正整数，要求找出相邻两个数字中差的绝对值最小的一对数字，如果有差的绝对值相同的，则输出最前面的一对数。
 *
 * 2<n<=100，正整数都在10^16范围内
 *
 * 输入
 * 输入包含2行，第一行为n，第二行是n个用空格分隔的正整数。
 *
 * 输出
 * 输出包含一行两个正整数，要求按照原来的顺序输出
 *
 *
 * 样例输入
 * 9
 * 1 3 4 7 2 6 5 12 32
 * 样例输出
 * 3 4
 */

public class Solution03 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] list = new int[n];
        for (int i = 0; i < n; i++) {
            list[i] = in.nextInt();
        }
        int[] old = list;
        sort(list);
        int temp = Integer.MAX_VALUE;
        int i1 = 0;
//        for (int i = 0; i < list.length-1; i++) {
//           if(ChaDeJDZ(list[i],list[i+1])< temp) {
//               temp = ChaDeJDZ(list[i],list[i+1]);
//              i1 = i;
//           }
//        }

        for (int i = 0; i < list.length-1; i++) {
            for (int j = 0; j < list.length-i-1; j++) {
                if(ChaDeJDZ(list[j],list[j+1])< temp) {
                    temp = ChaDeJDZ(list[j],list[j+1]);
                    i1 = j;
                }
            }
        }
        System.out.println(old[i1]+" "+old[i1+1]);
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
    public  static  int ChaDeJDZ(int x,int y){
        int temp = x-y;
        return abs(temp);
    }
}


