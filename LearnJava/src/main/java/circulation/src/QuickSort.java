package circulation.src; /**
 *  快速排序-不稳定的排序
 */
import java.util.Arrays;
public class QuickSort {
    public static void main(String[] args) {
        int[] a = {44, 78, 34, 56, 12};
        int len = a.length;
        for (int i = 0; i < len; i++) {//轮数
            int minIndex = i;//从下标0开始
            for (int j = i + 1; j < len; j++) { //j是下一个数
                if (a[minIndex] > a[j]) { //如果这个当前最小值的下标都大于下一个数
                    minIndex = j;//交换下标
                }
            }
            if (minIndex != i) {//如果下标是它自身，
                int temp1 = a[i];
                a[i] = a[minIndex];
                a[minIndex] = temp1;

            }
        }
        System.out.println(Arrays.toString(a));


    }
}