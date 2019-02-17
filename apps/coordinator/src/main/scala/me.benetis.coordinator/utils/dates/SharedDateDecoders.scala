package me.benetis.coordinator.utils.dates

import org.joda.time.DateTime
import me.benetis.coordinator.utils.dates.SharedDateEncoders._
import me.benetis.shared.{SharedDateTime, SharedTimeOnly}

object SharedDateDecoders {
  def toDateTime(millis: Long): DateTime = {
    new DateTime(millis)
  }
  def toSharedTimeOnly(millis: Long): SharedTimeOnly =
    toDateTime(millis).toSharedTimeOnly()

  def sharedDTToDT(sharedDateTime: SharedDateTime): DateTime =
    toDateTime(sharedDateTime.millis)
  def sharedDTToTimeOnly(sharedDateTime: SharedDateTime): SharedTimeOnly =
    toSharedTimeOnly(sharedDateTime.millis)
  def sharedTimeOnlyToDT(sharedTimeOnly: SharedTimeOnly): DateTime =
    DateFormatters.formatterTime.parseDateTime(sharedTimeOnly.timeString)
}
