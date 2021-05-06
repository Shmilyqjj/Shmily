package com.language;
/**
 * 语言国际化
 * Locale("zh","CN")   Locale("en","US")
 * ResourceBundle.getBundle(包名，Locale)
 * ResourceBundle 的 getString("关键字")
 * MessageFormat.format()传动态参数
 */

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LoginTest {
    public static void main(String[] args){
        Locale l1 = new Locale("zh","CN");
        Locale l2 = new Locale("en","US");
        Locale defaultL = Locale.getDefault();//选择系统默认语言

        ResourceBundle r = ResourceBundle.getBundle("com.language.lan",l1); //语言配置文件路径   一个参数代表默认语言 第二个参数代表目标语言的Locale对象
        System.out.println(r.getString("system.name"));
        System.out.println(r.getString("inputName"));
        Scanner in = new Scanner(System.in);
        String username = in.next();
        System.out.println(r.getString("inputPassword"));
        String pwd = in.next();

        if("admin".equals(username) && "123".equals(pwd))
        {
            System.out.println(r.getString("loginSuccess"));
            String str = r.getString("welcome");
            str = MessageFormat.format(str,username,"haha"); //动态参数 动态传参  传两个参数 与lan里的{0}{1}对应
            System.out.println(str);
        }else{
            System.out.println(r.getString("loginError"));
        }
    }
}
