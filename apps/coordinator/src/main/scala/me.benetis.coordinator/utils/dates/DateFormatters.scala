package me.benetis.coordinator.utils.dates

import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object DateFormatters {

  sealed trait CustomDateFormat
  case object CustomFormatDateTimeWithoutSeconds extends CustomDateFormat
  case object CustomFormatDateTime               extends CustomDateFormat
  case object CustomFormatTimeOnly               extends CustomDateFormat
  case object CustomFormatTimeOnlyWithoutSeconds extends CustomDateFormat

  val formatterDateTimeWithoutSeconds =
    DateTimeFormat.forPattern("YYYY-MM-dd HH:mm")
  val formatterTimeWithoutSeconds = DateTimeFormat.forPattern("HH:mm")

  val formatterTime = DateTimeFormat.forPattern("HH:mm:ss")

  val formatterDateOnly = DateTimeFormat.forPattern("yyyy-MM-dd")

  val formatterDateTime =
    DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss")

  def formatterForDateFormat(
      customDateFormat: CustomDateFormat): DateTimeFormatter = {
    customDateFormat match {
      case CustomFormatDateTimeWithoutSeconds => formatterDateTimeWithoutSeconds
      case CustomFormatDateTime               => formatterDateTime
      case CustomFormatTimeOnly               => formatterTime
      case CustomFormatTimeOnlyWithoutSeconds => formatterTimeWithoutSeconds
    }
  }

  def validationFuncForDateFormat(
      customDateFormat: CustomDateFormat): String => Boolean = {
    customDateFormat match {
      case CustomFormatDateTimeWithoutSeconds =>
        (dateValue: String) =>
          dateValue.matches("""^\d{4}-\d{2}-\d{2}\s\d{2}\:\d{2}$""")
      case CustomFormatDateTime =>
        (dateValue: String) =>
          dateValue.matches("""^\d{4}-\d{2}-\d{2}\s\d{2}\:\d{2}\:\d{2}$""")
      case CustomFormatTimeOnly =>
        (dateValue: String) =>
          dateValue.matches("""^\d{2}\:\d{2}\:\d{2}$""")
      case CustomFormatTimeOnlyWithoutSeconds =>
        (dateValue: String) =>
          dateValue.matches("""^\d{2}\:\d{2}$""")
    }
  }
}
