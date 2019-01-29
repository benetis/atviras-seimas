package me.benetis.shared.dates

import org.joda.time.DateTime
import me.benetis.shared.dates.SharedDateEncoders._

case class SharedDateTime(timestamp: String) {
  def toDateTime(): DateTime = SharedDateDecoders.toDateTime(timestamp)
  def toSharedTimeOnly(): SharedTimeOnly =
    SharedDateDecoders.toSharedTimeOnly(timestamp)
}
case class SharedDateOnly(timestamp: String)
case class SharedTimeOnly(time: String) {
  def toDateTime(): DateTime = DateFormatters.formatterTime.parseDateTime(time)
}

object SharedDateDecoders {
  def toDateTime(timestamp: String): DateTime = new DateTime(timestamp.toInt)
  def toSharedTimeOnly(timestamp: String): SharedTimeOnly =
    toDateTime(timestamp).toSharedTimeOnly()
}

//object DateWrapperDecoders {
//
//  implicit def sharedDateTimeToDateTime(
//      sharedDateTime: SharedDateTime): DateTime = {
//    val millisInSecond = 1000
//    new DateTime(sharedDateTime.timestamp * millisInSecond)
//  }
//
//  implicit def sharedDateOnlyToDateOnly(
//      sharedDateOnly: SharedDateOnly): DateTimeOnlyDate = {
//    val millisInSecond = 1000
//    DateTimeOnlyDate(new DateTime(sharedDateOnly.timestamp * millisInSecond))
//  }
//
//  implicit def sharedTimeOnly(
//      sharedTimeOnly: SharedTimeOnly): DateTimeOnlyTime = {
//    //"HH:mm:ss"
//    DateTimeOnlyTime(
//      DateFormatters.formatterTime.parseDateTime(sharedTimeOnly.time))
//  }
//
//}
