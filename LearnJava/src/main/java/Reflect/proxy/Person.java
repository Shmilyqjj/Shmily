/**
 * 实现类  实现IShopping接口
 */
package Reflect.proxy;

public class Person implements IShopping {
    @Override
    public void buy() {
        System.out.println("已经付款");
    }
}
