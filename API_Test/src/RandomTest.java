import java.util.Random;

/**
 * Random 类  此类的实例用于生成伪随机数流
 */
public class RandomTest {
    public static void main(String[] args) {
        Random r = new Random();
        System.out.println(r.nextInt()); //int最大多少 ，就在0-这个值中间产生一个随机数
        System.out.println(r.nextInt(50)); //0-50随机

        System.out.println(r.nextBoolean()+" "+r.nextDouble());
    }
}
