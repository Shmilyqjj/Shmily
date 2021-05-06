/**
 * 功能：基本数据类型包装类
 * date:2018.12.7
 */
public class IntegerTest {
    public static void main(String[] args) {
        Integer i1 = new Integer(10);  //装箱
        int i2 = i1.intValue();  //拆箱

        Character c1 = new Character('c');
        char c2 = c1.charValue();

        System.out.println(i2+" "+c2);

        //字符串转整形,浮点型
        int i3 = Integer.parseInt("1234567");
        float i4 = Float.parseFloat("12.56");
//        int a4 = Integer.parseInt("abc"); //不可以  异常
        System.out.println(i3+" "+i4);

        Integer i5 = new Integer(10);
        Integer i6 = new Integer(10);
        System.out.println(i5 == i6);
        Integer i7 = new Integer(128);
        Integer i8 = new Integer(128);
        System.out.println(i7 == i8);  //每次new都是对象，开辟新内存，所以不相等
        Integer i9 = 10;
        Integer i10 = 10;
        System.out.println(i9 == i10);//直接赋值还是对象  但是 享元模式，共享对象，尽可能减少内存使用量
        Integer i11 = 128;
        Integer i12 = 128;
        System.out.println(i11 == i12); //127 true      128 false   证明享元模式 小于一个字节可以共享，大于一个字节就不能
        System.out.println("--------------------------------------------------------------");
        System.out.println(i5.equals(i6)); //String和Integer等包装类的equals比较的是内容
        System.out.println(i5.hashCode() == i6.hashCode());

    }
}
