package time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 时间处理 线程安全的
 */
public class ThreadSafeTimeHandle {
    public static void main(String[] args) {

        DateTimeFormatter dataTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        //获取当前字符串时间
        String now = localDateTime.format(dataTimeFormatter);
        System.out.println("-------->当前字符串时间：" +now);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        //获取当前时间戳，秒数
        System.out.println("-------->当前时间戳(秒)：" + instant.getEpochSecond());
        //获取当前时间戳，毫秒数
        System.out.println("-------->当前时间戳(毫秒)：" +instant.toEpochMilli());
        //时间戳转换为时间字符串
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(instant.toEpochMilli() / 1000L, 0, ZoneOffset.ofHours(8));
        String timeStr = dataTimeFormatter.format(dateTime);
        System.out.println("-------->时间戳转时间字符串：" + timeStr);
        //时间字符串转时间戳
        long timeStamp = LocalDateTime.parse("2020-02-05 15:34:34", dataTimeFormatter).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        System.out.println("--------> 时间字符串转时间戳：" + timeStamp);
    }
}
