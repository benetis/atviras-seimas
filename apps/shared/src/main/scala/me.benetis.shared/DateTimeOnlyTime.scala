package me.benetis.shared

import org.joda.time.DateTime

case class DateTimeOnlyTime(time: DateTime)
case class DateTimeOnlyDate(date: DateTime)

object DateUtils {
  def timeWithDateToDateTime(time: DateTimeOnlyTime,
                             date: DateTime): DateTime = {
    val year  = date.year().get()
    val month = date.monthOfYear().get()
    val day   = date.dayOfMonth().get()

    val hours   = time.time.hourOfDay().get()
    val minutes = time.time.minuteOfHour().get()
    val seconds = time.time.secondOfMinute().get()

    new DateTime(year, month, day, hours, minutes, seconds)
  }
}
