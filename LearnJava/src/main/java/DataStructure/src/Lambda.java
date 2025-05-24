package DataStructure.src;

/**
 * Lambda表达式
 * Lambda允许把函数作为一个方法的参数（函数作为参数传递进方法中），或者把代码看成数据。Lambda表达式用于简化JAVA中接口式的匿名内部类。被称为函数式接口的概念。函数式接口就是一个具有一个方法的普通接口。像这样的接口，可以被隐式转换为lambda表达式。
 * date:2019.3.7
 * 语法： (参数1,参数2...) -> { ... }
 */
public class Lambda {
    public static void main(String[] args) {
        //第一种方式
        IEat e1 = new EatImpl();
        e1.eat();

        //第二种方式
        IEat e2 = new IEat() {
            @Override
            public void eat() {
                System.out.println("eat banana...");
            }
        };
        e2.eat();

        //第三种方式 - Lambda
        IEat e3 = ()->{
            System.out.println("eat pear...");
        };//大括号里只有一句话时可以省略大括号
        e3.eat();

        //带参数的Lambda表达式
        IEat1 e4 =(String name,String fruit)->{ //String类型可以省略
            System.out.println(name+" eat "+fruit);
        };
        e4.eat("zxw","shit");

        //有返回值的Lambda表达式
        IEat2 e5 = (name,fruit) ->10;
        int result = e5.eat("qjj","apple");
        System.out.println(result);

        //使用final关键字的Lambda
        IEat2 e6 = (final String name,String fruit)->10;
    }
}

interface IEat{
    public void eat();
}

interface IEat1{
    public void eat(String name,String fruit);
}

interface IEat2{
    public int eat(String name,String fruit);
}

class EatImpl implements IEat{

    @Override  //注解 - 帮助做检查
    public void eat() {
        System.out.println("eat apple...");
    }
}

class EatImpl1 implements IEat1{

    @Override  //注解 - 帮助做检查
    public void eat(String name,String fruit) {

    }
}