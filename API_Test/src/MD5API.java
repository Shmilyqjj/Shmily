import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * MD5的全称是Message-Digest Algorithm 5（信息-摘要算法）
 * MD5相关API
 * ctrl+alt+t快捷键可以选很多方式
 * date:2019.3.5
 */
public class MD5API {
    public static void main(String[] args) {
        String password = "qjj";
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5"); //ctrl+alt+t快捷键可以选很多方式
            byte[] bs = password.getBytes("utf-8");
            System.out.println(Arrays.toString(md5.digest(bs))); //输出md5数组
            System.out.println("------------------------------------");
            System.out.println(Arrays.toString(bs));//输出原数组
            System.out.println("------------------------------------");
            String newStr = Base64.getEncoder().encodeToString(md5.digest(bs));
            System.out.println(newStr);//输出base64加密的值
            System.out.println("------------------------------------");
            byte[] decode = Base64.getDecoder().decode(newStr);//base64解密得到md5
            System.out.println(Arrays.toString(decode));

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
