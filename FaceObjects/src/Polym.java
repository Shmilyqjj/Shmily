/**
 * 多态
 * Date:2018.11.18
 */
public class Polym {
    public static void main(String[] args) {
    Chicken hc = new HomeChicken("家鸡");
    Chicken wc = new WildChicken("野鸡");
    Chicken jc = new JJChicken("尖叫鸡");
    method(hc);
    method(jc);
    }
    public static void method(Chicken c){
        c.eat();
        if(c instanceof JJChicken){
            JJChicken jc = (JJChicken)c;
            jc.sing();
        }
    }
}

abstract class Chicken{
    protected String name;
    public Chicken(String name){
        this.name = name;
    }

    public abstract void eat();
}

class HomeChicken extends Chicken{
    public HomeChicken(String name){
        super(name);
    }
    public void eat(){
        System.out.println("我叫"+name+"，我吃小米");
    }
}

class WildChicken extends Chicken{
    public WildChicken(String name){
        super(name);//继承父类的时候会调用父类的构造函数 子类的构造函数只能用super
    }
    public void eat(){
        System.out.println("我叫"+name+"我吃肉");
    }
}

class JJChicken extends Chicken{
    public JJChicken(String name){
        super(name);
    }

    public void eat(){
        System.out.println("我叫"+name+"，我吃空气");
    }
    public void sing(){
        System.out.println("我可以勾勾勾");
    }

}
