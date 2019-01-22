package me.benetis.coordinator

import com.typesafe.scalalogging.LazyLogging
import me.benetis.shared.{DateTimeOnlyDate, DateTimeOnlyTime}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import scala.util.Try
import scala.xml.Node

sealed trait CustomDateFormat
case object DateTimeWithoutSeconds extends CustomDateFormat
case object DateTimeNormal         extends CustomDateFormat

package object downloader extends LazyLogging {

  val formatterDateTimeWithoutSeconds =
    DateTimeFormat.forPattern("YYYY-MM-dd HH:mm")
  val formatterTimeWithoutSeconds = DateTimeFormat.forPattern("HH:mm")

  val formatterDateTime =
    DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss")

  private def formatterForDateFormat(
      customDateFormat: CustomDateFormat): DateTimeFormatter = {
    customDateFormat match {
      case DateTimeWithoutSeconds => formatterDateTimeWithoutSeconds
      case DateTimeNormal         => formatterDateTime
    }
  }

  private def validationFuncForDateFormat(
      customDateFormat: CustomDateFormat): String => Boolean = {
    customDateFormat match {
      case DateTimeWithoutSeconds =>
        (dateValue: String) =>
          dateValue.matches("""^\d{4}-\d{2}-\d{2}\s\d{2}\:\d{2}$""")
      case DateTimeNormal =>
        (dateValue: String) =>
          dateValue.matches("""^\d{4}-\d{2}-\d{2}\s\d{2}\:\d{2}\:\d{2}$""")
    }
  }

  implicit class nodeExt(val o: Node) extends AnyVal {
    def tagText(tag: String): String = (o \ s"@$tag").text

    def validateDate(
        tag: String): Either[DomainValidation, DateTimeOnlyDate] = {
      val dateValue = tagText(tag)
      Either.cond(
        dateValue.matches("""^\d{4}-\d{2}-\d{2}$"""),
        DateTimeOnlyDate(new DateTime(dateValue)),
        BadDateFormat(tag)
      )
    }

    def validateDateTime(tag: String, customDateFormat: CustomDateFormat)
      : Either[DomainValidation, DateTime] = {

      val dateValue = tagText(tag)
      Either.cond(
        validationFuncForDateFormat(customDateFormat)(dateValue),
        formatterForDateFormat(customDateFormat).parseDateTime(dateValue),
        BadDateFormat(tag)
      )
    }

    def validateDateOrEmpty(
        tag: String): Either[DomainValidation, Option[DateTimeOnlyDate]] = {
      val dateValue = tagText(tag)

      val isValidDateOrEmpty = dateValue.matches("""^\d{4}-\d{2}-\d{2}$""") || dateValue.isEmpty

      Either.cond(
        isValidDateOrEmpty,
        if (dateValue.isEmpty) None
        else Some(DateTimeOnlyDate(new DateTime(dateValue))),
        BadDateAndNonEmptyFormat(tag)
      )
    }

    def validateDateTimeOrEmpty(
        tag: String): Either[DomainValidation, Option[DateTime]] = {
      val dateValue = tagText(tag)

      val isValidDateOrEmpty = dateValue.matches(
        """^\d{4}-\d{2}-\d{2}\s\d{2}\:\d{2}$""") || dateValue.isEmpty

      Either.cond(
        isValidDateOrEmpty,
        if (dateValue.isEmpty) None
        else Some(formatterDateTimeWithoutSeconds.parseDateTime(dateValue)),
        BadDateAndNonEmptyFormat(tag)
      )
    }

    def validateTimeOrEmpty(
        tag: String): Either[DomainValidation, Option[DateTimeOnlyTime]] = {
      val dateValue = tagText(tag)

      val isValidDateOrEmpty = dateValue.matches("""^\d{2}\:\d{2}$""") || dateValue.isEmpty

      Either.cond(
        isValidDateOrEmpty,
        if (dateValue.isEmpty) None
        else
          Some(
            DateTimeOnlyTime(
              formatterTimeWithoutSeconds.parseDateTime(dateValue))
          ),
        BadDateAndNonEmptyFormat(tag)
      )
    }

    def validateNonEmpty(tag: String): Either[DomainValidation, String] = {
      val fieldValue = tagText(tag)

      Either.cond(
        fieldValue.nonEmpty,
        fieldValue,
        EmptyField(tag)
      )
    }

    def validateInt(tag: String): Either[DomainValidation, Int] = {
      val fieldValue = tagText(tag)

      val result =
        if (Try(fieldValue.toInt).isFailure)
          Left(FieldIsNotAnInt(tag))
        else if (fieldValue.isEmpty)
          Left(EmptyField(tag))
        else
          Right(fieldValue.toInt)

      result
    }
  }

  def fetchLogIfErrorAndSaveWithSleep[T](
      insertF: Seq[T] => Unit,
      fetchF: () => Either[String, Seq[Either[DomainValidation, T]]]) = {
    fetchF() match {
      case Right(list) =>
        insertF(list.collect {
          case Right(value) => value
        })

        Thread.sleep(250)

        list.collect {
          case Left(err) => logger.warn(err.errorMessage)
        }
      case Left(err) => logger.error(err)

    }
  }
}
