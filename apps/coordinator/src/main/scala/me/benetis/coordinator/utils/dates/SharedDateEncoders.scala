package me.benetis.coordinator.utils.dates

import me.benetis.shared.{SharedDateOnly, SharedDateTime, SharedTimeOnly}

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
