package faceObjects;

/**
 * Java包装类  基本数据类型的包装类
 */
public class BaoZhuangClass {
    public static void main(String[] args) {
        Integer i = Integer.valueOf(5); //装箱
        Integer j = 6; //实际上是调用valueOf方法
        Character c = 'w';

        int ic = i;//拆箱
        int jc = j.intValue();//拆箱
        char cc = c.charValue();
        System.out.println(i+" "+j+" "+cc);
        System.out.println(ic+" "+jc+" "+cc);
    }
}
