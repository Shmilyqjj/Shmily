import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 定给一个整数数组nums 状语从句：一个目标值target，请在你该数组中找出状语从句：为目标值的那  两个  整数，并返回他们的数组下标。
 * 你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。
 *
 * 示例：
 * 给定nums = [2,7,11,15]，目标= 9
 * 因为nums [ 0 ] + nums [ 1 ] = 2 + 7 = 9
 * 所以返回[ 0,1 ]
 */
//1.暴力法
//public class LeetCode01 {
//    public static void main(String[] args) {
//        int target = 6;
//        int[] nums = {3,2,4};
//        System.out.println(Arrays.toString(twoSum(nums,target)));
//    }
//    public static int[] twoSum(int[] nums, int target) {
//        int[] result = {0,0};
//        int i,j;
//        for(i=0;i<nums.length;i++){
//            for(j=i+1;j<nums.length;j++){ //不能是同一个元素 所以j=i+1
//                if(nums[i] + nums[j] == target){
//                    result = new int[]{i,j};
//                }
//            }
//        }
//        return result;
//    }
//}

//-------------------------------------------------------------------------------------

//2.两遍哈希表 - 速度较快
//public class LeetCode01 {
//    public static void main(String[] args) {
//        int target = 9;
//        int[] nums = {2,7,11,15};
//        System.out.println(Arrays.toString(twoSum(nums,target)));
//    }
//
//    public static int[] twoSum(int[] nums,int target){
//        Map<Integer,Integer> map = new HashMap<>();
//        int[] result = {0,0};
//        for(int i=0;i<nums.length;i++){
//            map.put(nums[i],i); //把数组的所有数都存入hashmap
//        }
//        for(int i=0;i<nums.length;i++){
//            int temp = target - nums[i]; //temp为差值-为了在hashmap中找第二个值
//            if(map.containsKey(temp) && map.get(temp)!= i ){ //hashmap中存在对应的键满足条件且第二个数和第一个数不能重复
//               result = new int[] {i,map.get(temp)};
//            }
//        }
//        return result;
//    }
//}

//-------------------------------------------------------------------------------------

//3.一遍哈希表
public class LeetCode01 {
    public static void main(String[] args) {
        int target = 6;
        int[] nums = {3,3};
        System.out.println(Arrays.toString(twoSum(nums,target)));
    }

    public static int[] twoSum(int[] nums,int target) {
        Map<Integer, Integer> map = new HashMap<>();
        int[] result = {0,0};
        for(int i=0;i<nums.length;i++){
            int temp = target - nums[i];
            if(map.containsKey(temp)){
                result = new int[] {i,map.get(temp)};
            }
            map.put(nums[i],i);
        }
        return result;
    }
}