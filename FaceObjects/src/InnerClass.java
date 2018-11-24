//内部类
//内部类可以作为一个类的成员外，还可以把类放在方法内定义。
//1.方法内部类只能在定义该内部类的方法内实例化，不可以在此方法外对其实例化。 2.方法内部类对象不能使用该内部类所在方法的非final局部变量。
//1.不能有构造方法，只能有一个实例。 2.不能定义任何静态成员、静态方法。 3.不能是public,protected,private,static 4.一定是在new的后面，用其隐含实现一个接口或继承一个类。 5.匿名内部类为局部的，所以局部内部类的所有限制都对其生效。
public class InnerClass {
    public static void main(String[] args) {
    Outer o = new Outer();
    Outer.SInner os= new Outer.SInner();//静态内部类可以直接用类名调用，不用实例化（成员内部类要以对象名.类名 调用）
    os.print2();
    o.say();//方法内部类 在方法内部调用了该类
    o.print4();//继承式匿名内部类
    o.print5();//接口式内部类
    o.print6(new Animal(){//参数式匿名内部类
        public void speak(){
            System.out.println("参数式匿名内部类");
        }
        });
    }
}
class Outer{
    private class Inner{ //成员内部类 设为私有可以仅供类内访问
        public void print1(){
            System.out.println("成员内部类");
        }
    }

    static class SInner{ //静态内部类 在一个类内部定义一个静态内部类：
    //静态的含义是该内部类可以像其他静态成员一样，没有外部类对象时，也能够访问它。静态嵌套类仅能访问外部类的静态成员和方法。
        public void print2(){System.out.println("静态内部类");}
    }

    public void say(){
        final int i = 10; //java1.8后可以省略final
        class Inner1{
            public void print3(){
                System.out.println("方法内部类"+i);
            }
        }
        Inner1 i1 = new Inner1();
        i1.print3();
    }

    public void print4(){
        Animal cat = new Animal() { //继承式匿名内部类
            public void speak(){
                System.out.println("继承式匿名内部类");
            }
        }; //记得分号
        cat.speak();
    }

    public void print5(){  //接口式内部类
        IAnimal ai = new IAnimal() {
            public void speak() {
                System.out.println("接口式内部类");
            }
        }; //分号
        ai.speak();
    }

    public void print6(Animal a){ //参数式匿名内部类
        a.speak();
    }
}

abstract class Animal{
    public abstract void speak();
}
interface IAnimal{
    public abstract void speak();
}