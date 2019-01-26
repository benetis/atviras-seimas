package me.benetis.coordinator.utils
import DateFormatters._
import io.getquill.MappedEncoding
import me.benetis.shared.{DateTimeOnlyDate, DateTimeOnlyTime}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object SQLDateEncodersDecoders {

  implicit val encodeDateTime =
    MappedEncoding[DateTime, String](_.toString(formatterDateTime))

  implicit val decodeDateTime =
    MappedEncoding[String, DateTime](
      DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.0").parseDateTime)

  implicit val encodeDate =
    MappedEncoding[DateTimeOnlyDate, String](_.date.toString(formatterDateOnly))

  implicit val decodeDate =
    MappedEncoding[String, DateTimeOnlyDate](x =>
      DateTimeOnlyDate(formatterDateOnly.parseDateTime(x)))

  implicit val encodeTime =
    MappedEncoding[DateTimeOnlyTime, String](_.time.toString("HH:mm:ss"))

}
