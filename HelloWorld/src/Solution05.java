import java.util.Scanner;

/**
 * 贝壳找房笔试在线编程题 03
 * 题目描述：
 * 举重大赛开始了，为了保证公平，要求比赛的双方体重较小值要大于等于较大值的90%，那么对于这N个人最多能进行多少场比赛呢，任意两人之间最多进行一场比赛。
 *
 * 输入
 * 第一行N，表示参赛人数（2<=N<=10^5）
 *
 * 第二行N个正整数表示体重（0<体重<=10^8）
 *
 * 输出
 * 一个数，表示最多能进行的比赛场数
 *
 *
 * 样例输入
 * 5
 * 1 1 1 1 1
 * 样例输出
 * 10
 */


public class Solution05 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] weights = new int[n];
        for (int i = 0; i < n; i++) {
            weights[i] = in.nextInt();
        }

        int count =0;
            for (int i = 0; i < n-1; i++) {
                for (int j = 0; j < n-i-1; j++) {
                    if(weights[i]>weights[j] && (double)weights[j]>= weights[i]*0.9){
                        count++;
                    }
                    if(weights[i]<weights[j] && (double)weights[i]>= weights[j]*0.9){
                        count++;
                    }else{
                        count++;
                    }

                }

            }
        System.out.println(count);

    }
}
