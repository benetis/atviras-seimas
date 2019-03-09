package me.benetis.coordinator.utils
import me.benetis.coordinator.utils.dates.DateFormatters._
import io.getquill.MappedEncoding
import me.benetis.coordinator.utils.dates._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import me.benetis.coordinator.utils.dates.SharedDateEncoders._
import me.benetis.shared.{SharedDateOnly, SharedDateTime, SharedTimeOnly}

object SQLDateEncodersDecoders {

  implicit val encodeDateTime =
    MappedEncoding[SharedDateTime, String](d =>
      SharedDateDecoders.sharedDTToDT(d).toString(formatterDateTime))

  implicit val decodeDateTime =
    MappedEncoding[String, SharedDateTime](
      d =>
        DateTimeFormat
          .forPattern("YYYY-MM-dd HH:mm:ss.0")
          .parseDateTime(d)
          .toSharedDateTime())

  implicit val encodeDate =
    MappedEncoding[SharedDateOnly, String](d =>
      new DateTime(d.millis).toString(formatterDateOnly))

  implicit val decodeDate =
    MappedEncoding[String, SharedDateOnly](x =>
      formatterDateOnly.parseDateTime(x).toSharedDateOnly())

  implicit val encodeTime =
    MappedEncoding[SharedTimeOnly, String](d =>
      SharedDateDecoders.sharedTimeOnlyToDT(d).toString("HH:mm:ss"))

  implicit val decodeTime =
    MappedEncoding[String, SharedTimeOnly](d =>
      formatterTime.parseDateTime(d).toSharedTimeOnly())



}
