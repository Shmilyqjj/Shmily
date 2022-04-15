package Threads.threadlocal;

import javafx.scene.input.DataFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * :Description: 基于SimpleDateFormat和ThreadLocal实现线程安全的DateUtil工具类
 * :Author: 佳境Shmily
 * :Create Time: 2022/4/12 23:36
 * :Site: shmily-qjj.top
 * ThreadLocal是解决线程安全问题一个很好的思路，它通过为每个线程提供一个独立的变量副本解决了变量并发访问的冲突问题。
 * 在很多情况下，ThreadLocal比直接使用synchronized同步机制解决线程安全问题更简单，更方便，且结果程序拥有更高的并发性。
 * Synchronized机制采用了“以时间换空间”的方式，而ThreadLocal采用了“以空间换时间”的方式
 */
public class ThreadLocalDateUtil {
    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private static final ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>();

    public static DateFormat getDateFormat(){
        DateFormat df = threadLocal.get();
        if(df == null){
            df = new SimpleDateFormat(dateFormat);
            threadLocal.set(df);
        }
        return df;
    }

    public static String format(Date date) throws ParseException{
        return getDateFormat().format(date);
    }

    public static Date parse(String strDate) throws ParseException{
        return getDateFormat().parse(strDate);
    }

}
