package Tests;

import java.util.Date;

/**
 * :Description: a=a+b与a+=b有什么区别吗?
 * :Author: 佳境Shmily
 * :Create Time: 2022/4/12 22:42
 * :Site: shmily-qjj.top
 * +=操作符会进行强制隐式自动类型转换
 * a=a+b则不会自动进行类型转换
 */
public class AaddB {
    public static void main(String[] args) {
        short a = 127;
        short b = 127;
        try{
            // a = a + b; //java: 不兼容的类型: 从int转换到byte可能会有损失
            short s1 = 1;
            // s1 = s1 + 1;  short类型在进行运算时会自动提升为int类型 int不能赋值给short
            s1 += 1;  //不会报错
            System.out.println(s1);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println(a + b);
            a += b;
            System.out.println(a);
        }

        byte c = 127;
        byte d = 127;
        c += d;  // c+d计算时转换为int计算，但赋值给c时强转为byte溢出 c = (byte)(c+d)
        System.out.println(c);


    }
}
