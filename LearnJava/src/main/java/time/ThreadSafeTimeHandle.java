package time;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间处理 线程安全的
 */
public class ThreadSafeTimeHandle {
    public static final Map<String, String> dateFormatPatterns = new HashMap<String, String>() {{
        // 常见的日期时间格式
        put("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}", "yyyy-MM-dd HH:mm:ss.SSS");
        put("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", "yyyy-MM-dd HH:mm:ss");
        put("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}", "yyyy/MM/dd HH:mm:ss");
        put("\\d{4}-\\d{2}-\\d{2}", "yyyy-MM-dd");
        put("\\d{4}/\\d{2}/\\d{2}", "yyyy/MM/dd");
        put("\\d{8}", "yyyyMMdd");
        put("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}", "yyyy-MM-dd'T'HH:mm:ss");
        put("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}", "yyyy-MM-dd'T'HH:mm:ss.SSS");
        put("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}[+-]\\d{2}:\\d{2}", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        put("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}", "yyyy-MM-dd'T'HH:mm:ssXXX");
        put("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}", "dd-MM-yyyy HH:mm:ss");
        put("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}", "dd/MM/yyyy HH:mm:ss");
        put("\\d{2}-\\d{2}-\\d{4}", "dd-MM-yyyy");
        put("\\d{2}/\\d{2}/\\d{4}", "dd/MM/yyyy");
    }};

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

        LocalDateTime ldt = getDateTime("2024-05-21 05:21:21");
        System.out.println(ldt);
        ldt = getDateTime("20240521");
        System.out.println(ldt);
    }


    /**
     * 通用方法 通过时间字符串获取LocalDateTime对象
     * @param dtStr 时间字符串
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTime(String dtStr) {
        if (StringUtils.isEmpty(dtStr)) {
            return null;
        }
        String format = getDateFormat(dtStr);
        if ("yyyy-MM-dd".equals(format) || "yyyy/MM/dd".equals(format) || "yyyyMMdd".equals(format)) {
            // 对于仅有日期的情况，手动补充时间部分
            LocalDate date = LocalDate.parse(dtStr, DateTimeFormatter.ofPattern(format));
            return date.atStartOfDay();
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(dtStr, formatter);
        }
    }

    /**
     * 根据时间字符串获取日期格式
     * @param dtStr 时间字符串
     * @return string format
     */
    public static String getDateFormat(String dtStr) {
        for (Map.Entry<String, String> entry : dateFormatPatterns.entrySet()) {
            if (dtStr.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("no matched date format at dataUtils.dateFormatPatterns");
    }

}
