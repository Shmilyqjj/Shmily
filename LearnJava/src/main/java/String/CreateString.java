package String;

/**
 * 创建String 存放位置 以及 intern方法
 *
 * String的构造方法有四种-分别是String为参数，char为参数，StringBuffer为参数和StringBuilder为参数
 *
 * String的equals重写了逻辑  普通的equals底层只有 ==
 *
 * StringBuffer线程安全 但效率低，源码上 所有方法都加了synchronized
 *
 * final修饰String类型的好处 1.安全，不用担心被修改  2.效率：只有字符串是不可变时，才能实现字符串常量池，提高效率。
 *
 */
public class CreateString {
    public static void main(String[] args) {
        String s1 = "qjj";  //先去JVM常量池找是否已经有“qjj” 如果没有则存入JVM常量池
        String s2 = new String("qjj");  //创建String对象 到堆区
        final String s3 = "qjj";  //先去JVM常量池找是否已经有“qjj” 如果没有则存入JVM常量池 已经有了则指向那个地址

        System.out.println(s1 == s2);
        System.out.println(s1.equals(s2));  //equals判断值相等
        System.out.println(s1 == s3);   //指向同一个地址  两个string对象相等

        s2.intern();  //调用intern 会把字符串保存到常量池中

        /**
         * JDK 1.7之后，永久代 改为 元空间
         * 字符串常量池从方法区移到堆区
         */

        String s4 = "q" + "jj";  // 编译器优化了string拼接
        System.out.println(s1 == s4);  //True


        /**
         * 字符串对比：
         * equals对比String的值是否相等
         * == 比较的是字符串对象
         * compareTo是对字符串中的每个字符进行比较 ，不同的就返回差值（即返回int类型）
         */
        String s5 = "qjj";
        String s6 = "gjj";
        System.out.println(s5.compareTo(s6));

    }
}
