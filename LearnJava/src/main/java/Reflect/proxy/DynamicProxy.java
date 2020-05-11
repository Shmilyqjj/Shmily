package Reflect.proxy;

/**
 * 动态代理
 * 动态代理是通过代理类Proxy  接口和实现类之间可以不直接发生关系，而可以在运行期实现动态关联
 */
public class DynamicProxy {
    public static void main(String[] args) {
        Person person = new Person();//买东西
        ProxyBuy pb = new ProxyBuy();  //代理
        IShopping shopping = (IShopping)pb.create(person); //shopping代理对象
        shopping.buy(); //直接调用被代理类的方法
    }
}
