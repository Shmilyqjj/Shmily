/**
 *请实现一个函数，将一个字符串中的每个空格替换成“%20”。例如，当字符串为We Are Happy.则经过替换之后的字符串为We%20Are%20Happy。
 */
public class Problem2 {
    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer("abcd e fg");
        String ss = sb.toString();
        String rs = ss.replaceAll(" ","%20");
        System.out.println(rs);

        System.out.println("--------------------------------");

        Solution2 s2 = new Solution2();
        System.out.println(s2.replaceSpace(sb));
    }


}

class Solution2 {
    public String replaceSpace(StringBuffer str) {
        String s = str.toString();
        StringBuffer sbout = new StringBuffer();
        char[] chars = s.toCharArray();
        for(int i=0;i<s.length();i++){
            if(chars[i] == ' '){  //char是单引号 字符串是双引号
                sbout.append("%20");
            }else{
                sbout.append(chars[i]);
            }
        }
        return sbout.toString();
    }


}