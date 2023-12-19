package RegularExpression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    public static void main(String[] args) {
        System.out.println(isMatch("aaa20231122", "aaa[0-9]+.*"));
    }

    public static boolean isMatch(String str, String subStr) {
        Pattern p = Pattern.compile(subStr);
        Matcher m = p.matcher(str);
        return m.find();
    }
}
