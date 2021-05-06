/**
 * 代理类
 * 实现动态代理  需要继承InvocationHandler
 * 换其他类，因为是动态获取当前对象，forClass  所以代理类里面内容不用做任何更改
 */
package Reflect.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyBuy implements InvocationHandler {
    private Object target;//被代理的对象

    //创建代理对象 传入Object，意思是 要代理的是谁
    public Object create(Object target){
        this.target = target;
        Object ob =  Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),this); //三个参数：1.类加载器 2.接口数组(被代理对象继承了哪些接口，用getInterfaces方法获取)  3.代理类本身
        return ob;//返回代理对象
    }
    //类加载器原理：分三步：1.装载（拿到class信息，字节码，加载到内存） 2.链接：验证被加载类的正确定  3.初始化-把静态变量赋予正确的初始值
    /**
     * @param proxy 代理对象
     * @param method 被代理对象的方法
     * @param args 方法的参数
     */


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("查找商品");
        method.invoke(target);
        System.out.println("商品已经买到");
        return null;
    }
}
