package String;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * @author Shmily
 * @Description: 加解密工具类  （仅支持JDK8）
 * @CreateTime: 2024/8/26 下午5:53
 */

public class SecretUtils {

    private static final String UTF_8 = "UTF-8";

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

//    private static final BASE64Decoder b64Decoder = new BASE64Decoder();
//    private static final BASE64Encoder b64Encoder = new BASE64Encoder();
//
//    /**
//     * MD5加密
//     *
//     * @param src 要加密的串
//     * @return 加密后的字符串
//     */
//    public static String md5(String src) {
//        return md5(src, UTF_8);
//    }
//
//    /**
//     * MD5加密
//     *
//     * @param src     要加密的串
//     * @param charset 加密字符集
//     * @return 加密后的字符串
//     */
//    public static String md5(String src, String charset) {
//        try {
//            byte[] strTemp = StringUtils.isEmpty(charset) ? src.getBytes() : src.getBytes(charset);
//            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
//            mdTemp.update(strTemp);
//
//            byte[] md = mdTemp.digest();
//            int j = md.length;
//            char[] str = new char[j * 2];
//            int k = 0;
//
//            for (byte byte0 : md) {
//                str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
//                str[k++] = HEX_DIGITS[byte0 & 0xf];
//            }
//
//            return new String(str);
//        } catch (Exception e) {
//            throw new RuntimeException("MD5 encrypt error:", e);
//        }
//    }
//
//
//    /**
//     * BASE64解密
//     */
//    public static byte[] decryptBASE64(String key) throws Exception {
//        return b64Decoder.decodeBuffer(key);
//    }
//
//    /**
//     * BASE64加密
//     */
//    public static String encryptBASE64(byte[] key) {
//        return b64Encoder.encodeBuffer(key);
//    }
//
//    /**
//     * AES加密
//     *
//     * @param src       待加密字符串
//     * @param secretKey 密钥
//     * @param iv        向量
//     * @return 加密后字符串
//     */
//    public static String aesEncrypt(String src, String secretKey, String iv) {
//        if (StringUtils.isBlank(secretKey)) {
//            throw new RuntimeException("secretKey is empty");
//        }
//
//        try {
//            byte[] raw = secretKey.getBytes(UTF_8);
//            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
//            // "算法/模式/补码方式" ECB
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            IvParameterSpec iv1 = new IvParameterSpec(iv.getBytes());
//            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv1);
//            byte[] encrypted = cipher.doFinal(src.getBytes(UTF_8));
//            return encryptBASE64(encrypted);
//        } catch (Exception e) {
//            throw new RuntimeException("AES encrypt error:", e);
//        }
//
//    }
//
//    /**
//     * AES 解密
//     *
//     * @param src       待解密字符串
//     * @param secretKey 密钥
//     * @param iv        向量
//     * @return 解密后字符串
//     */
//    public static String aesDecrypt(String src, String secretKey, String iv) {
//        if (StringUtils.isBlank(secretKey)) {
//            throw new RuntimeException("secretKey is empty");
//        }
//        try {
//            byte[] raw = secretKey.getBytes(UTF_8);
//            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            IvParameterSpec iv1 = new IvParameterSpec(iv.getBytes());
//            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv1);
//            byte[] encrypted1 = decryptBASE64(src);
//            byte[] original = cipher.doFinal(encrypted1);
//            return new String(original, UTF_8);
//        } catch (BadPaddingException | IllegalBlockSizeException e) {
//            // 解密的原字符串为非加密字符串，则直接返回原字符串
//            return src;
//        } catch (Exception e) {
//            throw new RuntimeException("decrypt error，please check parameters", e);
//        }
//    }
//
//
//    public static String secretKey() {
//        try {
//            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//            keyGen.init(128);
//            SecretKey secretKey = keyGen.generateKey();
//            return encryptBASE64(secretKey.getEncoded());
//        } catch (Exception e) {
//            throw new RuntimeException("generate secretKey error", e);
//        }
//
//    }
//
//    public static boolean isNumeric(String str){
//        for (int i = str.length();--i>=0;){
//            if (!Character.isDigit(str.charAt(i))){
//                return false;
//            }
//        }
//        return true;
//    }
//
//
//
//    public static String shortUuid() {
//        StringBuffer shortBuffer = new StringBuffer();
//        String uuid = UUID.randomUUID().toString().replace("-", "");
//        for (int i = 0; i < 8; i++) {
//            String str = uuid.substring(i * 4, i * 4 + 4);
//            int x = Integer.parseInt(str, 16);
//            shortBuffer.append(chars[x % 0x3E]);
//        }
//        return shortBuffer.toString();
//    }
//
//    public static Integer string2Integer(String str) {
//        StringBuffer sb = new StringBuffer();
//        if (StringUtils.isBlank(str)) return null;
//        for (int i = 0; i < str.length(); i++) {
//            char c = str.charAt(i);
//            if (Character.isDigit(c)) {
//                sb.append(c);
//            }
//        }
//        return sb.length() > 0 ? Integer.parseInt(sb.toString()) : null;
//    }
//
//    public static void main(String[] args) throws Exception {
//        // base64 加解密
//        String s = "shmily123456--123456";
//        String encryptedStr = encryptBASE64(s.getBytes(StandardCharsets.UTF_8));
//        String decryptedStr = new String(decryptBASE64(encryptedStr));
//        System.out.printf("加密前: %s  加密后: %s  解密后: %s%n", s, encryptedStr, decryptedStr);
//    }

}

