//代理模式 Proxy Mode ：为其他对象提供一种代理以控制对这个对象的访问。
//代理模式说白了就是“真实对象”的代表，在访问对象时引入一定程度的间接性，因为这种间接性可以附加多种用途。
public class ProxyMode {
    public static void main(String[] args) {
        UserImpl user = new UserImpl();
        ProxyUserImpl p = new ProxyUserImpl(user);
        p.buy();
    }
}

class ProxyUserImpl implements  IShopping{ //代理类
    public IShopping is;//被代理的对象
    public ProxyUserImpl(IShopping is){
        this.is = is;
    }
    public void buy(){
        long StartTime = System.currentTimeMillis();
        is.buy();
        long EndTime = System.currentTimeMillis();
        System.out.println("一共花费时间"+(EndTime-StartTime)+"ms");
    }
}

interface IShopping{
    public void buy();
}
class UserImpl implements IShopping{
    public void buy() {
        for(int i=0;i<100;i++){
            System.out.println("Shopping");

        }
    }
}