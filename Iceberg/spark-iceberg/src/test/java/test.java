import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class test {
    public static void main(String[] args) {
        String[] sl = new String[]{"a","b","c"};
        System.out.println(Arrays.stream(sl).reduce((x, y) -> x + ";" + y).get());

        Map<Integer, A> am = new ConcurrentHashMap<>();
        am.put(1, new A("aa", 1));
        am.put(2, new A("aa", 1));
        am.put(2, new A("bb", 1));
        A a = am.get(2);
        a.setBb(3);
//        am.get(2).setBb(3);
        am.values().forEach(v -> System.out.println(v.getBb()));
    }
}

class A{
    private String aa;
    private int bb ;

    public A(String aa, int bb) {
        this.aa = aa;
        this.bb = bb;
    }

    public String getAa() {
        return aa;
    }

    public void setAa(String aa) {
        this.aa = aa;
    }

    public int getBb() {
        return bb;
    }

    public void setBb(int bb) {
        this.bb = bb;
    }
}
