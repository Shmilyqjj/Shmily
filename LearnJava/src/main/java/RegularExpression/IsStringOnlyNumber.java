package RegularExpression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式 采用单个字符串来描述.匹配一系列符合某个句法规则的字符串  正则表达式通常被用来检索.替换那些符合某个模式的文本
 * 包：java.util.regex
 * 判断字符串是否全由数字组成
 * Date:2019.3.26
 */
public class IsStringOnlyNumber {
    public static void main(String[] args) {

        //笨方法：
        String s = "520a1314";
        char[] chars = s.toCharArray(); //转换成字符数组
        boolean flag = true; //true表示全由数字组成
        for(int i=0;i<chars.length;i++){
            if(chars[i]<'0' || chars[i]>'9'){  //字符单引号
                flag = false;
                break;
            }
        }
        System.out.println(flag);

        //---------------------------------------------------------------------

        //正则表达式
        Pattern p = Pattern.compile("a*bc*"); //定义一个模板   a*代表 0 个或者多个a  bc代表结尾是bc
        Matcher m = p.matcher("aaaaaaaabc"); //Matcher用来检查是否符合这个模板
        boolean b = m.matches();//返回boolean  符合true  不符合false
        System.out.println(b);

        Pattern p0 = Pattern.compile("a?b?c");  //模板意思： ?代表0或者1个
        Matcher m0 = p0.matcher("ac");
        boolean b0 = m0.matches();
        System.out.println(b0);

        Pattern p1 = Pattern.compile("a{3}b*c?");  //模板意思： a{3}表示a只能出现3次，多了少了都是false
        Matcher m1 = p1.matcher("aaa");
        boolean b1 = m1.matches();
        System.out.println(b1);

        Pattern p2 = Pattern.compile("[^ab]c");  //模板意思： [ab]表示a和b都可以但只能出现一个  [^ab]除了a或者b以外都行,但只能一个字符
        Matcher m2 = p2.matcher("ttc");
        boolean b2 = m2.matches();
        System.out.println(b2);

        Pattern p3 = Pattern.compile("[a-z]*c");  //模板意思： [a-z]表示a到b都可以但只能出现一个   [0-9]数字都可以
        Matcher m3 = p3.matcher("tdadawdadawftc");
        boolean b3 = m3.matches();
        System.out.println(b3);

        Pattern p4 = Pattern.compile("\\w*");  //模板意思： \w代表a-z和A-Z和0-9所有的    \W与之相反   但是\是特殊符号所以再加个\ 转义
        Matcher m4 = p4.matcher("42sdadwaef9wda");
        boolean b4 = m4.matches();
        System.out.println(b4);

        Pattern p5 = Pattern.compile("\\d*");  //模板意思：\s所有空白符tab换行--1个   \d所有数字--1个
        Matcher m5 = p5.matcher("12827");
        boolean b5 = m5.matches();
        System.out.println(b5);

    }
}
