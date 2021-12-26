package DesignPatterns;

//适配器模式（ Adapter ）：将一个类的接口转换成客户希望的另外一个接口。适配器模式使得原本由于接口不兼容而不能一起工作的那些类可以一起工作。
//1.面向接口编程（面向抽象编程）
//2、封装变化
//3、多用组合，少用继承
//4、对修改关闭，对扩展开放
public class AdapterMode {
    public static void main(String[] args) {
    PowerB powerb = new PowerBImpl();
    PowerA powera = new AdapterPowerA(powerb);
    work(powera);
    }
    public static void work(PowerA pa){ //静态方法只能调用静态方法
        System.out.println("正在连接");
        pa.insert();
        System.out.println("工作结束");
    }
}

class AdapterPowerA implements PowerA{  //PowerA的适配器，将PowerB适配到PowerA
    private PowerB pb;
    public AdapterPowerA(PowerB pb){
        this.pb = pb;
    }
    public void insert() {
        pb.insert();
    }
}

interface PowerA{
    public void insert();
}
interface PowerB{
    public void insert();
}

class PowerAImpl implements PowerA{
    public void insert(){
        System.out.println("PowerA工作");
    }
}
class PowerBImpl implements PowerB{
    public void insert() {
        System.out.println("PowerB工作");
    }
}





//适配器模式的第二种写法：


interface IPerson{
    public void eat();
    public void swim();
    public void run();
    public void sing();
}
abstract class Adp implements IPerson{
    public void eat(){}
    public void swim(){}
    public void run(){}
    public void sing(){}
}

class Man extends Adp{  //其他类继承适配器
    public void sing(){

    }
    public void run(){}
}
class Woman implements IPerson{
    public void eat(){}
    public void swim(){}
    public void run(){}
    public void sing(){}
}