package faceObjects;

/**
 * 接口默认方法 default关键字 所有接口的实现类都能直接调用该方法
 * 接口里的静态方法可以通过接口直接调用
 * 静态方法和默认方法都可以有多个
 * date:2019.3.7
 */
public class InterfaceDefaultFunction {
    public static void main(String[] args) {
        IeatImpl ie = new IeatImpl();
        ie.say();
        Ieat.sing(); //直接调用静态方法
    }
}

interface Ieat{
    public default void say(){
        System.out.println("zxw吃肉");
    }

    public static void sing(){
        System.out.println("sing");
    }
}
class IeatImpl implements Ieat{

}