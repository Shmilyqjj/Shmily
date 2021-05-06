package com.study.spark.util

/**
  * 把数据封装成scala类
  * @param ip          info(0)  IP地址
  * @param time        info(1)  时间
  * @param categoryId  info(2)  类别编号
  * @param refer       info(3)  来源
  * @param statusCode  info(4)  状态码
  *
  *                   case加上才可以再其他类里调用  case匹配模式
  */
case class ClickLog(ip:String,time:String,categoryId:Int,refer:String,statusCode:Int)