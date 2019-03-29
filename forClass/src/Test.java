public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        Dog dog = new Dog("xiaobai",2,"black");

        //得到class类的对象有三种方式
        Class dogClass1 = dog.getClass(); //获得类信息  获得类名，属性，信息，方法
        Class dogClass2 = Dog.class;
        Class dogClass3 = Class.forName("Dog"); //括号里包名.类名

        //Class类进行对象的实例化
    }
}
