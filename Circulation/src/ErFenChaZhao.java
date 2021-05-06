/**
*功能:二分查找法
 * Date: 2018.11.13
*/
public class ErFenChaZhao {
    public static void main(String[] args) {
        int[] arr = {3,7,12,43,56,89};
        int out = selection(arr,89);//用二分查找法在arr中查找元素 7
        System.out.println(out);




    }
    public static int selection(int[] arr,int val){
        int start = 0;//开始
        int end = arr.length;//结束


        do{
            int i = (start+end)/2;//代表中间的数的下标
            if(val > arr[i]){
                start = i+1;
            }else if(val < arr[i]){
                end = i-1;
            }else{
                return i;
            }
        }while(start <= end);

        return -1;
    }
}
