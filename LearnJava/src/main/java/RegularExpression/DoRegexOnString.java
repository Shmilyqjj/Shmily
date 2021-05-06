package RegularExpression;

import java.util.Arrays;

/**
 * 在字符串上操作正则表达式
 * Date:2019.3.26
 */
public class DoRegexOnString {
    public static void main(String[] args) {
        String s = "5201314";
        boolean m = s.matches("\\d+");//告知字符串s是否匹配正则表达式  +多个
        System.out.println(m);

        String s1 = s.replaceFirst("1","x"); //用replacement替换正则表达式,替换1个
        String s2 = s.replaceAll("1","x"); //用replacement替换正则表达式,替换所有
        System.out.println(s1+" "+s2);

        String[] split = s.split("1"); //按1分隔
        for (String i:split) {
            System.out.print(i+" ");
        }
        System.out.println();

        String[] split0 = s.split("1",2); //按1分隔,分成两个
        System.out.println(Arrays.toString(split0));

        //examples
        isTelNumber itn = new isTelNumber();
        isPhoneNumber ipn = new isPhoneNumber();
        isIP isIP = new isIP();
        iswebsite iswebsite = new iswebsite();
    }
}

//1.验证电话号码
class isTelNumber{
    public isTelNumber(){
        String s = "010-383985612";
        boolean b = s.matches("\\d{3,4}-\\d{7,9}");  //   \\d 3-4位 中间-
        System.out.println(b);
        isUserName isUserName = new isUserName();

    }
}

//2.验证手机号码
class isPhoneNumber{
    public isPhoneNumber(){
        String s = "18642196696";
        boolean b = s.matches("1[3-9]\\d{9}");  //第一位必须是1  第二位3-9  其余9位
        System.out.println(b);
    }
}

//3.验证用户名，字母开头，数字，字母，下划线组合
class isUserName{
    public isUserName(){
        String s = "qjj123_456zxw";
        boolean b = s.matches("[a-zA-Z]\\w*");  //第一位必须是1  第二位3-9  其余9位
        System.out.println(b);
    }
}

//4.验证IP 192.168.1.1
class isIP{
    public isIP(){
        String s = "192.168.1.102";
        boolean b = s.matches("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}");
        System.out.println(b);
    }
}

//4.验证网址 https://www.baidu.com/
class iswebsite{
    public iswebsite(){
        String s = "https://www.baidu.com/";
        boolean b = s.matches("");
        System.out.println(b);
    }
}


//.......