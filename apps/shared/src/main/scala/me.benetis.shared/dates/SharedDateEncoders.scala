package me.benetis.shared.dates
import com.github.nscala_time.time.Imports._

object SharedDateEncoders {

  implicit class customDateTimeExtensions(val self: org.joda.time.DateTime)
      extends AnyVal {
    def toSharedDateTime(): SharedDateTime = {
      SharedDateTime(toTimestamp())
    }

    def toSharedDateOnly(): SharedDateOnly = {
      SharedDateOnly(toTimestamp())
    }

    def toSharedTimeOnly(): SharedTimeOnly = {
      SharedTimeOnly(self.toString("HH:mm:ss"))
    }

    private def toTimestamp(): String = (self.getMillis / 1000).toString
  }
}
