package JavaAPIs;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Shmily
 * @Description: 业界通用 ip转int和int转ip
 * @CreateTime: 2024/1/29 上午11:03
 */

public class IP2IntAndInt2IP {

    public static void main(String[] args) {
        // 数字转 IPv4 地址
        long number = 3967429803L;
        String ipv4FromNumber = numberToIpv4(number);
        System.out.println("Number: " + number);
        System.out.println("Number as IPv4: " + ipv4FromNumber);

        // IPv4 地址转数字
        String ipv4Address = ipv4FromNumber;
        long ipv4AsNumber = ipv4ToNumber(ipv4Address);
        System.out.println("IP Address: " + ipv4Address);
        System.out.println("IP as Number: " + ipv4AsNumber);

        // Ipv6 地址转数字
        String ipv6 = "FFFF:FFFF:7654:FEDA:1245:BA98:3210:4562";
        BigInteger bigInteger = ipv6ToInt(ipv6);
        System.out.println("IPv6 Address: " + ipv6);
        System.out.println("IPv6 as Number: " + bigInteger.toString());

        // 数字 转 Ipv6 地址
        String ipv6Str = intToIpv6(bigInteger);
        System.out.println("Number: " + bigInteger);
        System.out.println("Number as IPv6: " + ipv6Str);

    }

    // IP 地址转数字
    public static long ipv4ToNumber(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            byte[] bytes = inetAddress.getAddress();
            long result = 0;
            for (byte octet : bytes) {
                result <<= 8;
                result |= octet & 0xff;
            }
            return result;
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP Address", e);
        }
    }

    // 数字转 IP 地址
    public static String numberToIpv4(long number) {
        return ((number >> 24) & 0xFF) + "." +
                ((number >> 16) & 0xFF) + "." +
                ((number >> 8) & 0xFF) + "." +
                (number & 0xFF);
    }


    /**
     * ipv6 转bigInteger
     * @param ipv6 ipv6地址
     * @return biginteger数字
     */
    public static BigInteger ipv6ToInt(String ipv6){
        int compressIndex = ipv6.indexOf("::");
        if (compressIndex != -1){
            String part1s = ipv6.substring(0, compressIndex);
            String part2s = ipv6.substring(compressIndex + 1);
            BigInteger part1 = ipv6ToInt(part1s);
            BigInteger part2 = ipv6ToInt(part2s);
            int part1hasDot = 0;
            char ch[] = part1s.toCharArray();
            for(char c : ch){
                if(c == ':'){
                    part1hasDot++;
                }
            }
            return part1.shiftLeft(16 * (7 - part1hasDot)).add(part2);
        }
        String[] str = ipv6.split(":");
        BigInteger big = BigInteger.ZERO;
        for(int i = 0; i < str.length; i++){
            //::1
            if(str[i].isEmpty()){
                str[i] = "0";
            }
            big = big.add(BigInteger.valueOf(Long.valueOf(str[i], 16)).shiftLeft(16 * (str.length - i - 1)));
        }
        return big;
    }

    /**
     * BigInteger数 转为ipv6字符串
     */
    public static String intToIpv6(BigInteger big){

        String str = "";
        BigInteger ff = BigInteger.valueOf(0xffff);
        for (int i = 0; i < 8; i++){
            str = big.and(ff).toString(16) + ":" + str;
            big = big.shiftRight(16);
        }
        //去掉最后的：号
        str = str.substring(0, str.length() - 1);
        return str.replaceFirst("(^|:)(0+(:|$)){2,8}", "::");
    }


}
