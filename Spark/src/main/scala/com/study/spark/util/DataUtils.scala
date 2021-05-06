package com.study.spark.util

import java.util.Date

import org.apache.commons.lang3.time.FastDateFormat

/**
  * 爱奇艺视频数据分析可视化项目 - SparkStreaming时间工具
  *
  * 字段：IP地址-时间-URL地址-访问来源-状态码
  * 143.30.167.29   2019-08-04 02:11:36     "GET pianhua/130 HTTP/1.0"      -       301
  * 167.124.132.143 2019-08-04 02:11:36     "GET www/2 HTTP/1.0"    -       200
  * 把时间 2019-08-04 02:11:36 转为 20190804
  */

object DataUtils {
  val YYYYMMDDHHMMSS_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")  //源格式
  val TAG_FORMAT = FastDateFormat.getInstance("yyyyMMdd")   //目标格式

  /**
    * 把参数time的时间转化成时间戳
    * @param time
    * @return
    */
  def getTime(time:String) ={
    YYYYMMDDHHMMSS_FORMAT.parse(time).getTime
  }

  /**
    * 把参数time时间戳转换为目标格式
    * @param time
    * @return
    */
  def parseToMin(time:String)={
    TAG_FORMAT.format(new Date(getTime(time))) //调用转化时间戳函数
  }

  //测试类
  def main(args: Array[String]): Unit = {
    print(parseToMin("2019-08-04 02:11:36"));
  }

}
