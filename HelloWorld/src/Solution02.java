import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 网易笔试第三题
 * 输入第一行数字n 第二行是长度为n的数字序列，中间空格隔开
 * 输出n个数字，第i个数字表示k=i时的答案
 * 例子：
 * 输入
 * 6
 * 1 3 2 4 6 5
 * 输出：
 * 1 3 3 4 6 6
 */

public class Solution02 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();

        List<Integer> arrayList = new ArrayList();
        int[] num = new int[n];
        for (int i = 0; i < n; i++) {
            num[i] = in.nextInt();
        }
        for (int i = 0; i < n; i++) {
            arrayList.add(num[i]);
        }
        int temp = 0;
        for (int i = 0; i < n; i++) {

                if(temp<arrayList.get(i)){
                    System.out.print(arrayList.get(i)+" ");
                    temp = arrayList.get(i);
                }else{
                    System.out.print(temp+" ");
                }
        }
    }
}
