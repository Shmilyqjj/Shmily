import java.text.{ParseException, SimpleDateFormat}
import java.util.Date

object TestDate {
  def main(args: Array[String]): Unit = {
    val ts:Long = 1638460801000L
    println(handleDate(ts))
  }

  @throws[ParseException]
  private def handleDate(time: Long) = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    val date = new Date(time)
    val old = sdf.parse(sdf.format(date))
    val now = sdf.parse(sdf.format(new Date))
    val oldTime = old.getTime
    val nowTime = now.getTime
    val day = (nowTime - oldTime) / (24 * 60 * 60 * 1000)
    if (day < 1) { //今天
      val format = new SimpleDateFormat("HH:mm")
      format.format(date)
    }
    else if (day == 1) { //昨天
      val format = new SimpleDateFormat("HH:mm")
      "昨天 " + format.format(date)
    }
    else { //可依次类推
      val format = new SimpleDateFormat("yyyy/MM/dd")
      format.format(date)
    }
  }
}
