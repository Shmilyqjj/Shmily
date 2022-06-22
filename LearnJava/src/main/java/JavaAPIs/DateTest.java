package JavaAPIs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * 功能：日期显示 !!!
 * 注意:空白处Alt+Enter  ---- 自动导包
 */

public class DateTest {
    public static void main(String[] args) throws ParseException {
        Date date = new Date(); //java.util
        System.out.println(date);//显示日期

        //Calendar类显示时间 - 两种实例化方式
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = new GregorianCalendar(); //这个是Calendar的子类

        System.out.println(c1.get(Calendar.YEAR)); //c1.get(Calendar.YEAR)返回年
        System.out.println(c1.get(Calendar.MONTH)+1); //c1.get(Calendar.MONTH)返回月
        System.out.println(c1.get(Calendar.DAY_OF_MONTH)); //c1.get(Calendar.DAY)返回日
        System.out.println(c1.get(Calendar.HOUR_OF_DAY)); //c1.get(Calendar.HOUR_OF_DAY)返回时 - 24时制
        System.out.println(c1.get(Calendar.HOUR)); //c1.get(Calendar.HOUR)返回时 - 12时进制

        System.out.println(c1.get(Calendar.MINUTE)); //c1.get(Calendar.MINUTE)返回分
        System.out.println(c1.get(Calendar.SECOND)); //c1.get(Calendar.SECOND)返回秒
        System.out.println(c1.get(Calendar.MILLISECOND)); //c1.get(Calendar.MILLISECOND)返回毫秒


        DateFormat df = new SimpleDateFormat(); //时间格式化

        System.out.println(df.format(date));  //传入Date的对象，格式变化


        DateFormat df1 = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss S"); //时间格式化-按模版  - 字母表示查手册
        System.out.println(df1.format(date));  //传入Date的对象，格式变化


        System.out.println("######################################################################");
        printAvailableTimezone();
        String dateStr = timeConvertWithTimezone(System.currentTimeMillis(), "Asia/Shanghai", " Asia/Tokyo ", "yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(dateStr);



    }

    /**
     * 带时区的时间转换format
     * @param ts 时间戳
     * @param sourceTimeZone 源时区
     * @param targetTimeZone 转换输出的目标时区
     * @param format SimpleDateFormat的参数 指定格式
     * @return
     */
    public static String timeConvertWithTimezone(long ts, String sourceTimeZone, String targetTimeZone, String format){
        TimeZone.setDefault(TimeZone.getTimeZone(sourceTimeZone.trim()));
        DateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone(targetTimeZone.trim()));
        return df.format(ts);
    }

    /**
     * 输出可用时区
     */
    public static void printAvailableTimezone(){
        String[] ids = TimeZone.getAvailableIDs();
        for (String id:ids) {
            System.out.println(id);
        }
    }
}


