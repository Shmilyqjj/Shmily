/**
 * 代理类
 * 实现动态代理  需要继承InvocationHandler
 */
package com.proxy;

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
