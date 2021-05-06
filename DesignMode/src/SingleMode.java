/**
 * 单例模式
 * 专门提供一个工厂来提供某个对象的实例，对象的创建交个一个工厂去创建
 * 单例模式三部曲（1.构造方法私有化，让除了自己类能创建其他地方都不能创建2.在自己类中创建一个单例-创建时需要进行方法同步3.提供一个方法获取该实例的对象）.单例有几种：饱汉模式（一出来就实例化），饥汉模式（需要的时候才创建）
 */
public class SingleMode {
    public static void main(String[] args) {
        PersonFactory.getPersonFactory().toString();//饱汉模式-一出来就创建
    }
}

class PersonFactory {

    //构造方法私有化-让除了自己类能创建其他地方都不能创建
    private PersonFactory() {

    }

    //在自己类中创建一个单例-创建时需要进行方法同步
    private static PersonFactory pf = new PersonFactory();

    //提供方法获取-pf是静态对象，获取pf的方法就得是静态方法
    public static PersonFactory getPersonFactory() {
        return pf;
    }

    //饥汉模式
//    public synchronized static PersonFactory getPersonFactory() {
//        if (pf == null){  //用的时候再创建
//            PersonFactory pf = new PersonFactory();
//        }
//        return pf;
//    }

}
