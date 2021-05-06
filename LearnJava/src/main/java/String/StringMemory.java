package String;

public class StringMemory {
    public static void main(String[] args) {
        String s1 = "飞飞";  //直接定义的String字符串--存在于字符串常量区（字符串常量池）
        //下面的语句创建了几个对象
        String s2 = new String("飞飞"); //如果单这一句话，没有前面的 s1=飞飞，则这句话创建了两个对象（开辟两个内存空间-一个是new，一个是在字符串常量区创建“飞飞”） 前面有了s1飞飞，这句话只创建一个对象：new一个
        String s3 = "飞飞";
        System.out.println(s1 == s3);  //s1.s3都存在于字符串常量区，所以，是同一个对象存在于常量区
        System.out.println("---------------");

        //String类的编译期和运行期：！！！！
        // 编译期可确定的值，用已有的值，编译期不能确定，创建一个新的对象
        String a = "a";//编译期可确定的值
        String a1 = a + "1";//编译期不能确定，创建一个新的对象
        String a2 = "a1";//编译期可确定的值
        System.out.println(a1 == a2); //不同内存空间-false

        final String b = "b"; //final 编译期确定
        String b1 = b + 1; //b确定  b+1也确定
        String b2 = "b1";
        System.out.println(b1 == b2);

        String c = getC();
        String c1 = c +1;
        String c2 = "c1";
        System.out.println(c1 == c2);

        final String d = getD();//函数只有运行时期才能确定值
        String d1 = d +1;
        String d2 = "d1";
        System.out.println(d1 == d2);


        String A = "a"; //1
        String B = "b"; //2
        String C = A + B + 1; //A+B  A+B+1  C - 3 4 5
        System.out.println(C); //这个过程一共创建了5个对象

    }
    public static String getC(){
        return "c";
    }
    public static String getD(){
        return "d";
    }
}