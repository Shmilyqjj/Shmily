package Threads.threadlocal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * :Description: 基于SimpleDateFormat和ThreadLocal实现线程安全的DateUtil工具类
 * :Author: 佳境Shmily
 * :Create Time: 2022/4/12 23:36
 * :Site: shmily-qjj.top
 * ThreadLocal是解决线程安全问题一个很好的思路，它通过为每个线程提供一个独立的变量副本解决了变量并发访问的冲突问题。
 * 在很多情况下，ThreadLocal比直接使用synchronized同步机制解决线程安全问题更简单，更方便，且结果程序拥有更高的并发性。
 * Synchronized机制采用了“以时间换空间”的方式，而ThreadLocal采用了“以空间换时间”的方式
 */
public class ConcurrentDateUtil {
    private static ThreadLocal<DateFormat> threadLocal = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    );

    public static Date parse(String dateStr) throws ParseException {
        return threadLocal.get().parse(dateStr);
    }


    public static String format(Date date) {
        return threadLocal.get().format(date);
    }
}
