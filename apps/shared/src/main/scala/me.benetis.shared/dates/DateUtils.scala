package me.benetis.shared.dates
import org.joda.time.DateTime
import me.benetis.shared.dates.SharedDateEncoders._

object DateUtils {
  def timeWithDateToDateTime(time: SharedTimeOnly,
                             date: SharedDateTime): SharedDateTime = {

    val timeD = time.toDateTime()
    val dateD = date.toDateTime()

    val year  = dateD.year().get()
    val month = dateD.monthOfYear().get()
    val day   = dateD.dayOfMonth().get()

    val hours   = timeD.hourOfDay().get()
    val minutes = timeD.minuteOfHour().get()
    val seconds = timeD.secondOfMinute().get()

    new DateTime(year, month, day, hours, minutes, seconds).toSharedDateTime()
  }
}
