package faceObjects;

import java.util.Arrays;

public class SystemClass {
    public static void main(String[] args) {
        System.err.println("hahaha"); //红色

        int[] a1 = {1,2,3,4,5,6,7};
        int[] a2 = new int[a1.length]; //定义a2长度是a1的长度
        System.arraycopy(a1,0,a2,1,5);
        System.out.println(Arrays.toString(a2));
        System.out.println(System.currentTimeMillis());//1970-1-1 00：00 到现在的毫秒数
        System.out.println(System.getProperty("java.home"));//getProperty()获取系统属性
        System.out.println(System.getProperties());


//        System.exit(1); //1异常退出  0正常退出
    }

}
