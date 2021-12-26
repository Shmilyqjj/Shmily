package HelloWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 快手03  DP:0-1背包问题
 */
public class solution12 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] l = new int[n];
        int total = 0;
        for (int i = 0; i < n; i++) {
            l[i] = sc.nextInt();
            total += l[i];
        }
        int count = 0;
        List arr = new ArrayList<Integer>();
        int temp = total/2;;
        for (int i = 0; i < n; i++) {
            count += l[i];

            if(count < temp){
                temp = temp + l[i];

            }
        }


    }
}
