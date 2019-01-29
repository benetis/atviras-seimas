package me.benetis.shared.dates

import org.joda.time.DateTime
import me.benetis.shared.dates.SharedDateEncoders._

case class SharedDateTime(millis: Long) {
  def toDateTime(): DateTime = SharedDateDecoders.toDateTime(millis)
  def toSharedTimeOnly(): SharedTimeOnly =
    SharedDateDecoders.toSharedTimeOnly(millis)
}
case class SharedDateOnly(millis: Long)
case class SharedTimeOnly(timeString: String) {
  def toDateTime(): DateTime =
    DateFormatters.formatterTime.parseDateTime(timeString)
}

object SharedDateDecoders {
  def toDateTime(millis: Long): DateTime = {
    new DateTime(millis)
  }
  def toSharedTimeOnly(millis: Long): SharedTimeOnly =
    toDateTime(millis).toSharedTimeOnly()
}
