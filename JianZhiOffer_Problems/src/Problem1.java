import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * 在一个二维数组中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。
 * 请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。
 */
public class Problem1 {
    public static void main(String[] args) {
        Solution1 s = new Solution1();
        int [][] array = new int[][]{
                {1,2},{2,3},{3,4},{4,5},{5,6},{6,7},{7,8},{8,9},{9,10},{10,11},
                {11,12},{12,13},{13,14},{14,15},{15,16},{16,17},{17,18},{18,19},{19,20},{20,21},
        };
        boolean b = s.find(25,array);
        System.out.println(b);
    }

}
class Solution1{
    public boolean find(int target,int[][] array){
        int height = array.length-1;
        int i = 0;
        while(height >= 0 && i<=array[0].length-1){
            if(array[height][i]>target){
                height --;
            }else{
                if(array[height][i]<target)
                    i++;
                else{
                    return true;
                }
            }
        }
        return false;
    }
}
