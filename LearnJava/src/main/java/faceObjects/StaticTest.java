package faceObjects;

/**
 * static对象/方法 详解
 * 参考链接https://www.cnblogs.com/dolphin0520/p/3799052.html
 */
//虽然在静态方法中不能访问非静态成员方法和非静态成员变量，但是在非静态成员方法中是可以访问静态成员方法/变量的。
//static变量也称作静态变量，静态变量和非静态变量的区别是：静态变量被所有的对象所共享，在内存中只有一个副本，它当且仅当在类初次加载时会被初始化。
//static代码块：static关键字还有一个比较关键的作用就是 用来形成静态代码块以优化程序性能。static块可以置于类中的任何地方，类中可以有多个static块。在类初次被加载的时候，会按照static块的顺序来执行每个static块，并且只会执行一次。
//static是不允许用来修饰局部变量。


//Test1:  面试1：下面代码的输出结果是什么
//public class faceObjects.StaticTest extends Base{
//    static{//静态代码块
//        System.out.println("Main static");
//    }
//    public faceObjects.StaticTest(){//构造函数
//        System.out.println("Main constructor");
//    }
//    public static void main(String[] args){
//        new faceObjects.StaticTest();
//    }
//}
//
//class Base{
//    static{
//        System.out.println("Base static");
//    }
//    public Base(){
//        System.out.println("Base constructor");
//    }
//}

//1.先加载StaticTest类，但是它继承Base类所以先加载Base类
//2.发现有static块，先加载static块，然后继续加载StaticTest类
//3.mian方法new一个StaticTest，便执行构造器，因为继承于Base所以先执行Base类的构造函数
//4.Base类构造函数执行完再执行StaticTest类的构造函数


//Test2面试：输出结果？
//
//public class faceObjects.StaticTest {
//    Person person = new Person("Test");//3person static   4person test
//    static{
//        System.out.println("test static");//1
//    }
//
//    public faceObjects.StaticTest() {
//        System.out.println("test constructor");//5
//    }
//
//    public static void main(String[] args) {
//        new MyClass();
//    }
//}
//
//class Person{
//    static{
//        System.out.println("person static");
//    }
//    public Person(String str){
//        System.out.println("person "+str);
//    }
//}
//class MyClass extends faceObjects.StaticTest{
//    Person person = new Person("MyClass");//person Myclass
//    static{
//        System.out.println("myclass static");//2  实例化时先执行类中的static方法再去执行它的父类
//    }
//
//    public MyClass(){
//        System.out.println("myclass constructor");//7
//    }
//}
//首先加载Test类，因此会执行Test类中的static块。接着执行new MyClass()，而MyClass类还没有被加载，因此需要加载MyClass类。在加载MyClass类的时候，发现MyClass类继承自Test类，但是由于Test类已经被加载了，所以只需要加载MyClass类，那么就会执行MyClass类的中的static块。
//在加载完之后，就通过构造器来生成对象。而在生成对象的时候，必须先初始化父类的成员变量，因此会执行Test中的Person person = new Person()，而Person类还没有被加载过，因此会先加载Person类并执行Person类中的static块，接着执行父类的构造器，完成了父类的初始化，然后就来初始化自身了，因此会接着执行MyClass中的Person person = new Person()，最后执行MyClass的构造器。


//Test3
public class StaticTest{
    static{
        System.out.println("test static 1");
    }
    public static void main(String[] args) {

//    static{   //static块不能出现在方法内部
//        //....
//        }
//
    }

    static{
        System.out.println("test static 2");
    }
}
//static块可以出现类中的任何地方（只要不是方法内部，记住，任何方法内部都不行），并且执行是按照static块的顺序执行的。