package me.benetis.coordinator.utils.dates

import org.joda.time.DateTime
import me.benetis.coordinator.utils.dates.SharedDateEncoders._
import me.benetis.shared.{
  SharedDateOnly,
  SharedDateTime,
  SharedTimeOnly
}

object SharedDateDecoders {
  def toDateTime(millis: Long): DateTime = {
    new DateTime(millis)
  }
  def toSharedTimeOnly(millis: Long): SharedTimeOnly =
    toDateTime(millis).toSharedTimeOnly()
  def toSharedDateOnly(millis: Long): SharedDateOnly =
    toDateTime(millis).toSharedDateOnly()

  def sharedDOToDT(
    sharedDateOnly: SharedDateOnly
  ): DateTime =
    toDateTime(sharedDateOnly.millis)
  def sharedDTToDT(
    sharedDateTime: SharedDateTime
  ): DateTime =
    toDateTime(sharedDateTime.millis)
  def sharedDTToTimeOnly(
    sharedDateTime: SharedDateTime
  ): SharedTimeOnly =
    toSharedTimeOnly(sharedDateTime.millis)
  def sharedDTToDateOnly(
    sharedDateTime: SharedDateTime
  ): SharedDateOnly =
    toSharedDateOnly(sharedDateTime.millis)
  def sharedTimeOnlyToDT(
    sharedTimeOnly: SharedTimeOnly
  ): DateTime =
    DateFormatters.formatterTime.parseDateTime(
      sharedTimeOnly.timeString
    )
  def sharedDOToSharedDT(
    sharedDateOnly: SharedDateOnly
  ): SharedDateTime = {
    SharedDateTime(sharedDateOnly.millis)
  }
}
