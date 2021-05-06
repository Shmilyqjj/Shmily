/**
 * Description:给定两个字符串 s 和 t ，编写一个函数来判断 t 是否是 s 的字母异位词。
 * Date: 2020/4/30 13:25
 * Author: 佳境Shmily
 * Site: shmily-qjj.top
 *
 * 题目链接：https://leetcode-cn.com/problems/valid-anagram/
 *
 * 示例 1:
 * 输入: s = "anagram", t = "nagaram"
 * 输出: true
 *
 * 示例 2:
 * 输入: s = "rat", t = "car"
 * 输出: false
 *
 * 说明:
 * 你可以假设字符串只包含小写字母。
 *
 * 进阶:
 * 如果输入字符串包含 unicode 字符怎么办？你能否调整你的解法来应对这种情况？
 *
 * 思路： 使用两个初始大小26的int数组保存字母出现频次  再依次对比  保存index位置利用ascii确定
 */
public class LeetCode242 {
    public static void main(String[] args) {
        String s = "anagram", t = "nagaram";
//        String s1 = "rat", t1 = "car";
//        System.out.println('s' - 'a');   // 18 a为0则s为18
//        System.out.println('a' + 0);   //转ascii
        System.out.println(LeetCode242.isAnagram(s, t));
    }


    public static boolean isAnagram(String s, String t) {
        if(s.length() != t.length()){
            return false;
        }else{
            // s t的长度一定相等
            final int len = s.length();
            final int num = 26;
            int[] sArray = new int[num];
            int[] tArray = new int[num];
            for (int i = 0; i < len; i++) {
                sArray[s.charAt(i) - 'a'] ++;
                tArray[t.charAt(i) - 'a'] ++;
            };
            for (int i = 0; i < num; i++){
                if(sArray[i] != tArray[i]){
                    return false;
                }
            }
            return true;
        }

    }
}
