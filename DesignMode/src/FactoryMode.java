//简单工厂模式是由一个工厂对象决定创建出哪一种产品类的实例。简单工厂模式是工厂模式家族中最简单实用的模式。
public class FactoryMode {
    public static void main(String[] args) {
        Production p = ProductFactory.getProduct("phone");
        p.work();
        String data ="aaa";
        Production p1 = ProductFactory.getProduct(data);
        if(null != p1){
            p1.work();
        }else{
            System.out.println("不生产"+data);
        }

    }
}
abstract class Production{
    public abstract void work();
}

class ProductFactory{

    public static Production getProduct(String data){ //静态方法可以直接访问

        if(data.equals("phone")){
            return new Phone();
        }
        if(data.equals("computer")){
            return new Computer();
        }
        return null;
    }
}

class Phone extends Production{
    public void work(){
        System.out.println("手机正在工作");
    }
}
class Computer extends Production{
    public void work(){
        System.out.println("电脑正在工作");
    }
}
