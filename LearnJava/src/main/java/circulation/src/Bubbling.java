package circulation.src;

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

        System.out.println("------------------------------");

        maopao2 m2 = new maopao2();
        m2.maopaopaixu(arr);
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

}

class maopao2{
    public int arr[];
    public void maopaopaixu(int[] arr){
          this.arr = arr;
          int len =arr.length;
          for(int i=0;i<len;i++){//控制轮数
              for(int j=0;j<len-i-1;j++){ //第一轮要交换4次len-i-1 第二轮要交换三次...所以
                if(arr[j] > arr[j+1]){
                     int temp = arr[j];
                     arr[j] = arr[j+1];
                     arr[j+1] = temp;
                }
              }
          }
        System.out.println(Arrays.toString(arr));
    }
//44 78 34 56 12 比较相邻
//44 34 78 56 12
//44 34 56 78 12
//44 34 56 12 78 第一轮结束
//34 44 56 12 78
//34 44 12 56 78 第二轮结束
//34 12 44 56 78 第三轮结束
//12 34 44 56 78 第四轮结束
}