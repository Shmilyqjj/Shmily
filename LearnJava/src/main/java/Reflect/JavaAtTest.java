package Reflect;
/**
 * Java 无主函数测试代码的方法  @Test注释
 * 在要测试的方法前加@Test，如果多个方法需要测试，每个都要加
 * 需要Junit.jar 这个jar包
 * Junit的@Test不能测试静态方法
 * --------------------------------------
 * java反射
 */

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class JavaAtTest {
    @Test
    public  void test(){
        System.out.println("123456789");
    }

    @Test
    public void forClassTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            Dog dog = new Dog("xiaobai",2,"black");
            Class dogClass  = Class.forName("Dog");

            //newInstance会调用无参数构造方法进行实例化
            Dog d = (Dog)dogClass.newInstance();//返回Object类型，需要强转
            System.out.println(d);//输出类的信息--默认使用无参数构造函数

            //getConstructors()调用有参构造方法实例化
            Constructor[] constructors = dogClass.getConstructors(); //返回类型：构造器类型的数组-（构造方法类型的数组）
            for (int i = 0; i < constructors.length; i++) {
                System.out.println(constructors[i].getName()+" 构造方法有"+constructors[i].getParameterCount()+"个参数 访问修饰符为:"+ Modifier.toString(constructors[i].getModifiers())); //获取构造方法的getName方法   getParameterCount()获取构造方法的参数个数  constructors[i].getModifiers()得到int类型值，用Modifier.toString转换为访问修饰符
             }

            //调用有参数的构造函数
        Constructor constructor = dogClass.getConstructor(String.class, int.class, String.class);
        Dog dd = (Dog)constructor.newInstance("xiaohei", 3, "white");
        System.out.println(dd);

        System.out.println("------------------------------------------------");


    }
}
