import java.util.Arrays;

/**
 * 功能：选择排序法
 * 每一趟从待排序的数据元素中选出最小（或最大）的一个元素，顺序放在已排好序的数列的最后，直到全部待排序的数据元素排完。
 * 选择排序是不稳定的排序方法。
 * Date:2018.11.13
 */
public class SelectionSort {
    public static void main(String[] args){
        int[] arr = {44,78,34,56,12};
        int len = arr.length;

    for(int i=0;i<len;i++){
        int minIndex = i;//当前最小值对应的下标
        for(int j=i+1;j<len;j++){//交换下标
           if(arr[minIndex] > arr[j]){
               minIndex = j;
           }
        }
        if(minIndex != i){//交换数
            int temp =arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
    }
        System.out.println(Arrays.toString(arr));
    }
}

//思想:待排序的最小的放到已排序的最后面
//44 78 34 56 12  开始
//12 78 34 56 44  第0轮 默认最小下标是0 minIndex = 0 -> 12比78小不变 12比34大交换下标 34 78 44 56 12 -> 44的下标变为2 -> 44比56小不变 44比12大交换下标 44的下标变为4 12的下标变为0  minIndex=4
//12 34 78 56 44  第1轮  下标0不管了 从minIndex= 1开始
//12 34 44 56 78  第2轮
