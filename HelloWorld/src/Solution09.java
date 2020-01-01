import java.util.ArrayList;
import java.util.Scanner;

/**
 * 题目描述：
 * 某公司雇有N名员工，每名员工可以负责多个项目，但一个项目只能交由一名员工负责。现在该公司接到M个项目，令Ai,j表示第i名员工负责第j个项目所带来的收益，那么如果项目分配得当，总收益最大是多少？
 * 第一行包含两个整数N和M，1≤N，M≤1000。
 *
 * 接下来N行，每行包含M个整数，第i行的第j个整数表示Ai,j，1≤Ai,j≤1000。
 *
 * 输出
 * 输出总收益的最大值。
 *
 *
 * 样例输入
 * 3 3
 * 1 3 3
 * 2 2 2
 * 3 2 1
 * 样例输出
 * 9
 */

// 18%
public class Solution09 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        int[][] arr = new int[n][m];
        ArrayList<Integer> arrayList = new ArrayList<Integer>(n+m);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                arr[i][j] = sc.nextInt();
                arrayList.add(arr[i][j]) ;
            }
        }

        int size = arrayList.size();
        int[] a = new int[size];
        for (int i = 0; i < size; i++) {
            a[i] = arrayList.get(i);
        }
        sorted(a);
        int result = 0;
        for (int i = 0; i < m; i++) {
            result += a[i];
        }
        System.out.println(result);

    }

    public static void sorted(int[] a){
        int temp;
        for (int i = 0; i < a.length-1; i++) {
            for (int j = 0; j < a.length-1-i; j++) {
                if(a[j+1]>a[j]){
                    temp = a[j];
                    a[j] = a[j+1] ;
                    a[j+1] = temp;

                }
            }
        }
    }

}


