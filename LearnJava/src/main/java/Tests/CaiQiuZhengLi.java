package Tests; /**
 * 题目描述：
 * 小明家有一些彩球，一段时间后小明玩耍时将它们无序的散落在家中，一天，小明想对其进行整理，规则为一个篮子中只放一种颜色彩球，可有多个篮子放同一颜色的球，每个篮子里的球不少于2个。
 * 假设小明整理好后，能使各篮子中彩球数量是相同的，则认为小明整理好了。用一个数字表示一种颜色彩球，一组数表示小明已经找到了的彩球，问小明用找到的全部彩球能按规则整理好么？
 * 输入
 * 第一行彩球总数: n,  2<n<10000
 *
 * 第二行一段整数ai,  1<ai<10000  (排除ai全部相等的情况)
 *
 * 输出
 * 若能整理好，最小篮子数；否则0
 *
 *
 * 样例输入
 * 6
 * 1 1 2 2 2 2
 * 样例输出
 * 3
 *
 * 样例输入:
 * 6
 * 1 1 2 2 2 2
 * 样例输出:
 * 3      	tip: [1, 1] [2, 2] [2, 2]
 * 样例输入:
 * 5
 * 1 1 1 2 2
 * 样例输出:
 * 0     		tip: [1 1 1] [2 2]
 * 样例输入:
 * 6
 * 1 1 1 2 2 2
 * 样例输出:
 * 2      	tip: [1 1 1] [2 2 2]
 */

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class CaiQiuZhengLi {


    /*请完成下面这个函数，实现题目要求的功能
    当然，你也可以不按照下面这个模板来作答，完全按照自己的想法来 ^-^
    ******************************开始写代码******************************/
    static int main(int n,int a[]) {
        int num = 0;

        return num;
    }
    /*******************************结束写代码*******************************/

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        int res;
        int n = in.nextInt();
        int[] a = null;
        for (int i = 0; i < n; i++) {
            a[i] = in.nextInt();
        }
        res = main(n,a);
        System.out.println(String.valueOf(res));

    }
}
