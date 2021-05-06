package Reflect;
/**
 * Test标注在多个方法前的时候，鼠标在哪就运行当前方法，不全部运行
 *
 */

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GetPackageTest {
    //取得类所在的包
    @Test
    public void Test(){
        Class Dogclass0 = Dog.class;
        Package aPackage = Dogclass0.getPackage();//没包名会报空指针异常
        System.out.println(aPackage.getName());
    }

    //取得类所在的属性
    @Test
    public void Test0(){
        Class Dogclass = Dog.class;
//        Field[] fields = Dogclass.getFields();//查看所有属性(不过只能获取到非私有属性)
        Field[] declaredFields = Dogclass.getDeclaredFields();//查看任何属性，所有的！
        for (int i = 0; i < declaredFields.length; i++) {
            System.out.println(declaredFields[i].getName());
        }
    }

    //取得类的所有方法
    @Test
    public void Test1() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        Class DogClass = Class.forName("Dog");
//        Method[] methods = DogClass.getMethods();//得到当前类的方法以及继承的所有公共方法
        Method[] declaredMethods = DogClass.getDeclaredMethods();
        for (Method i:declaredMethods) {
            System.out.println(i.getName());
        }

        System.out.println("--------------------------------------------------------");

        Dog dog = new Dog("xiaohei",3,"pink");
        //选择特定的方法去调用
        for (int i = 0; i < declaredMethods.length; i++) {
            if(declaredMethods[i].getName().equals("toString")){
                System.out.println(declaredMethods[i].invoke(dog));//invoke方法：代表执行这个方法,invoke括号里填入对象，表示在哪个方法上调用这个对象
            }
        }
        for (int i = 0; i < declaredMethods.length; i++) {
        if(declaredMethods[i].getName().equals("set")) {
            declaredMethods[i].setAccessible(true);//去掉修饰符-让属性对外部可见
            declaredMethods[i].invoke(dog);
        }
        }
    }

}
