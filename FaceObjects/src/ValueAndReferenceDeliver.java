/**
 * 值传递和引用传递
 * 注意java堆内存栈内存的原理
 */
public class ValueAndReferenceDeliver {
    public static void main(String[] args){

        //值传递-值传递----方法调用时，实际参数把它的值传递给对应的形式参数，函数接收的是原始值的一个copy，此时内存中存在两个相等的基本类型，即实际参数和形式参数，后面方法中的操作都是对形参这个值的修改，不影响实际参数的值。
        int x = 10;
        method(x);
        System.out.println("经过值传递后x="+x);

        //引用传递----也称为传地址。方法调用时，实际参数的引用(地址，而不是参数的值)被传递给方法中相对应的形式参数，函数接收的是原始值的内存地址；
            //在方法执行中，形参和实参内容相同，指向同一块内存地址，方法执行中对引用的操作将会影响到实际对象。
        Duck d = new Duck();
        method(d);
        System.out.println("经过引用传递后Duck age = "+d.age);

        //String传递  --  String虽然是个类但可以理解为是值传递，操作形参不改变实参的对象
        String name = "小境";
        method(name);
        System.out.println("String传递后name = "+name);

       //String传递+引用传递
        Person0 p = new Person0();
        method(p);
        System.out.println("经过传递后的p.name="+p.name);
    }
    public static void method(int mx){//函数接收的是原始值的copy
        mx = 20;//只改变这个copy 原始的x未变
    }
    public static void method(Duck md){//形参接收的是实参的地址（引用传递-传地址） 此时形参实参指向同一个内存地址
        md.age = 5;//修改参数的值对形参和实参都有影响
    }
    public static void method(String ms){ //String类似基本类型，值传递，不会改变实际参数的值
        ms = "小文";
    }
    public static void method(Person0 mp){
        mp.name = "zxw";
    }
}
class Duck{
    int age = 2;
}

class Person0{
    String name = "qjj";
}

//String, Integer, Double等immutable的类型特殊处理，可以理解为传值，最后的操作不会修改实参对象。