package me.benetis.shared.dates
import com.github.nscala_time.time.Imports._

object SharedDateEncoders {

  implicit class customDateTimeExtensions(val self: org.joda.time.DateTime)
      extends AnyVal {
    def toSharedDateTime(): SharedDateTime = {
      SharedDateTime(self.getMillis)
    }

    def toSharedDateOnly(): SharedDateOnly = {
      SharedDateOnly(self.getMillis)
    }

    def toSharedTimeOnly(): SharedTimeOnly = {
      SharedTimeOnly(self.toString("HH:mm:ss"))
    }

  }
}
