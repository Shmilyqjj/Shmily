import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        String[] sl = new String[]{"a","b","c"};
        System.out.println(Arrays.stream(sl).reduce((x, y) -> x + ";" + y).get());
    }
}
