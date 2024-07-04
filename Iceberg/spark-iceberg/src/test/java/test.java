import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) {
        Pattern p = Pattern.compile("gateway_sign_20240101");
        Matcher m = p.matcher("gateway_sign_20240101");
        System.out.println(m.find());
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
