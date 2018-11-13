import java.util.Arrays;

/**
 * 冒泡排序--稳定的排序
 */
public class Bubbling {
    public static void main(String[] args) {


        int[] a = {44, 78, 56, 23, 12};
        int len = a.length;
        for (int i = 0; i < len; i++) {//轮数，有几个数就排几轮
            for (int j = i + 1; j < len; j++) {
                if (a[i] > a[j]) {
                    int temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
        System.out.println(Arrays.toString(a));//将数组以字符串的形式输出
        System.out.println("------------------------------");

        int[] arr = {44, 78, 34, 56, 12};

        maopao m = new maopao();
        m.maopaopaixu(arr);
    }
}
class maopao {
    public int arr[];
    public void maopaopaixu(int[] arr) {
        this.arr = arr;
        int leng = arr.length;
        int temp;
        for (int i = 0; i < leng; i++) {//轮数，有几个数就排几轮
            for (int j = i + 1; j < leng; j++) {
                if (arr[i] > arr[j]) {
                    temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        System.out.println(Arrays.toString(arr));
    }
    //44 78 34 56 12  待排序
    //44 78 34 56 12
    //34 78 44 56 12
    //12 78 44 56 34   第一轮-下标0的依次与后面的元素比，如果大于后边的元素就交换
    //12  78 44 56 34  第二轮 - 下标1的依次与后面元素比如果大于就交换
    //12  44 78 56 34
    //12  44 78 56 34
    //12  34 78 56 44  第二轮结束
    //12 34  78 56 44  第三轮开始时 -下标1依次跟后面比
    //12 34  56 78 44
    //12 34  44 78 56  第三轮结束
    //12 34 44  78 56  第四轮开始
    //12 34 44  56 78  第四轮结束
}