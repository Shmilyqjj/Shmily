package HelloWorld;

import java.util.Scanner;

public class Solution08 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[][] arr = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                arr[i][j] = sc.nextInt();
            }
        }

        int count = 0;
        int temp = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(arr[i][j] >= 3) count ++;
                if(arr[i][j] == arr[j][i]) continue;
                if(arr[i][j] == 0) count++;
            }

        }
        System.out.println(count);

    }

}
