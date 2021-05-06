import sun.font.TrueTypeFont;

/**
 * Description: LeetCode 151.翻转字符串里的单词
 * Date: 2020/4/30 13:03
 * Author: 佳境Shmily
 * Site: shmily-qjj.top
 *
 * 题目链接：https://leetcode-cn.com/problems/reverse-words-in-a-string/
 */
public class LeetCode151 {
    public static void main(String[] args) {
        LeetCode151 solution = new LeetCode151();
        System.out.println(solution.reverseWords("  hello world!  "));
        System.out.println(solution.reverseWords("the sky      is blue"));
    }

    public String reverseWords(String s) {
        //方法1

        //trim()不仅可以去掉字符串两端空格，还能去掉其他一些多余的符号：\t  \n  \v  \f  \r  \x0085  \x00a0  ?  \u2028  \u2029
        //正则替换多个空格为一个空格
        s = s.trim().replaceAll("\\s+", " ");
        String[] s1 = s.split(" ");
        StringBuffer result = new StringBuffer();
        for (int i = s1.length - 1; i >= 0; i--) {
            result.append(s1[i] + " ");
        }
        return result.toString().trim();
    }

}
