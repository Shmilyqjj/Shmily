import java.util.Scanner;

public class Solution07 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] td = new int[n];
        for (int i = 0; i < n; i++) {
            td[i] = sc.nextInt();
        }
        bubble(td);
        int temp = 0;
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
//                System.out.println(td[i] + " " + td[j]);  //得到所有二个元素的组合(无重复)





            }
        }
        System.out.println(count);
    }


    public static void bubble( int[] arr){
        int temp;
        for (int i = 0; i < arr.length; i++) {//轮数，有几个数就排几轮
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
    }
        public static int GYS ( int x, int y){
            if (x > y) {
                int temp = x;
                x = y;
                y = temp;
            }
            while (x != 0) {
                int temp = y % x;
                y = x;
                x = temp;
            }
            return y;
        }

}
