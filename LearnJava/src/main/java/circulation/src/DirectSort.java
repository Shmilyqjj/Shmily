package circulation.src;

import java.util.Arrays;

/**
 * 直接插入排序算法
 * Date:2018.11.13
 * 每步将一个待排序的记录，按其顺序码大小插入到前面已经排序的子序列的合适位置（从后向前找到合适位置后），直到全部插入排序完为止。（不是从最后向前找）
 */
public class DirectSort {
    public static void main(String[] args){
         int[] arr = {44,78,34,56,12};
         int len = arr.length;
         for(int i=1;i<len;i++){//轮数
             int temp = arr[i];//相对来说第二个数
             for(int j=i-1;j>=0;j--){  //arr[j]相对来说是第一个数
                 if(arr[j]>temp){
                   arr[j+1] = arr[j];
                 }else{
                     arr[j+1] = temp;
                     break;
                 }
                 if(j==0){//如果temp比到开头了都还小，则放到开头
                     arr[j] = temp;
                 }
             }
         }
        System.out.println(Arrays.toString(arr));



    }
}


//44 78 34 56 12  temp = 78 78比44大不动（第一轮结果）
//34 44 78 56 12  第二轮结果  temp = 34 -> 34比78小，将78向后挪1位 temp=34 34比44小，44向后挪一位 34替换44
//34 44 56 78 12  第三轮结果  temp = 56 -> 56比78小，将78向后挪1位 temp=56 56比44大，56替换44后面的78的位置
//12 34 44 56 78  第四轮结果  temp = 12 -> 12比78小，将78向后挪1位 temp=12 12比56小，56向后挪1位 -> 12比44小，44向后挪1位 -> 12比34小，34向后挪1位 12开头