import oracle.jrockit.jfr.StringConstantPool;

import java.util.Arrays;

/**
 * 功能：一些String 字符串的常用API
 */
public class StringAPI {
    public static void main(String[] args) {
        String str = "dfafd45246sba2fd323asf";
        System.out.println(str.charAt(1)); //charAt(1)  返回下标为1的字符
        System.out.println(Arrays.toString(str.toCharArray()));
        char[] cs = {'1','4','6'};
        String s1 = new String(cs,0,2);  //从下标0开始，找2个
        System.out.println(s1);
        System.out.println(str.startsWith("dfa",4)); //startsWith 判断以什么开头，4是从第四个位置开始找   是true 不是false
        System.out.println(str.replace("fd","*")); //替换指定字符

        System.out.println("----------------------");

        System.out.println(str.replaceAll("[0-9]","*")); //regex正则表达式  [0-9]数字0-9变为*  等价于 //d

        System.out.println(str.substring(4)); //截取字符串 - 从下标4开始截后面的
        System.out.println(str.substring(4,7));//截取字符串 - 从下标4-7

        System.out.println(str.indexOf("a")); //查找字符a  返回下标 ，如果多个a ，返回第一个a的下标

        System.out.println("------------------------");

        System.out.println(Arrays.toString(str.split("s",2))); //分割字符串 - 以s分割

        System.out.println(str.indexOf("adasdfd"));//查找字符串  找不到返回-1
        String s3 = "   d    fdfd fdfd       ";
        System.out.println(s3.trim());//去开头结尾的空格，中间的不去掉

        System.out.println("-----------------");

        String s = "";
        System.out.println(s.isEmpty()); //判断是否为空

        String s0 = "AbAbCdCdEfg";
        System.out.println(s0.toLowerCase());//全转小写
        System.out.println(s0.toUpperCase());//全转大写

        String sss = "1e12e23e34e45e56be7ace8";  //Split方法
        String[] es = sss.split("e");
        for (String sp:es) {
            System.out.print(sp+" ");
        }
        System.out.println("---------------------------------");
        String[] es1 = sss.split("e",3);
        for (String sp1:es1) {
            System.out.print(sp1+" ");
        }
        System.out.println("---------------------------------");

        String S_indexof = "1a2a3a4a5aa89abcsa"; //获得所有a的位置索引
        System.out.println(S_indexof.indexOf("a"));   // 但indexOf方法只返回第一个索引
        //所以用if方法
        char[] C_indexOf = S_indexof.toCharArray();
        for (int i =0;i<C_indexOf.length;i++){
            if(C_indexOf[i] == 'a'){
                System.out.print(i+" ");
            }
        }
        System.out.println();




    }
}
