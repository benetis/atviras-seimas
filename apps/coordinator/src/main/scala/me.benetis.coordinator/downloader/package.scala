package me.benetis.coordinator

import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import scala.util.Try
import scala.xml.Node
import me.benetis.coordinator.utils.dates.DateFormatters._
import me.benetis.coordinator.utils.dates._
import me.benetis.coordinator.utils.dates.SharedDateEncoders._
import me.benetis.shared.{SharedDateOnly, SharedDateTime, SharedTimeOnly}

package object downloader extends LazyLogging {

  implicit class nodeExt(val o: Node) extends AnyVal {
    def tagText(tag: String): String = (o \ s"@$tag").text

    def validateDate(tag: String): Either[DomainValidation, SharedDateOnly] = {
      val dateValue = tagText(tag)
      Either.cond(
        dateValue.matches("""^\d{4}-\d{2}-\d{2}$"""),
        new DateTime(dateValue).toSharedDateOnly(),
        BadDateFormat(tag)
      )
    }

    def validateDateTime(tag: String, customDateFormat: CustomDateFormat)
      : Either[DomainValidation, SharedDateTime] = {

      val dateValue = tagText(tag)
      Either.cond(
        validationFuncForDateFormat(customDateFormat)(dateValue),
        formatterForDateFormat(customDateFormat)
          .parseDateTime(dateValue)
          .toSharedDateTime(),
        BadDateFormat(tag)
      )
    }

    def validateDateOrEmpty(
        tag: String): Either[DomainValidation, Option[SharedDateOnly]] = {
      val dateValue = tagText(tag)

      val isValidDateOrEmpty = dateValue.matches("""^\d{4}-\d{2}-\d{2}$""") || dateValue.isEmpty

      Either.cond(
        isValidDateOrEmpty,
        if (dateValue.isEmpty) None
        else Some(new DateTime(dateValue).toSharedDateOnly()),
        BadDateAndNonEmptyFormat(tag, dateValue)
      )
    }

    def validateDateTimeOrEmpty(
        tag: String): Either[DomainValidation, Option[SharedDateTime]] = {
      val dateValue = tagText(tag)

      val isValidDateOrEmpty = dateValue.matches(
        """^\d{4}-\d{2}-\d{2}\s\d{2}\:\d{2}$""") || dateValue.isEmpty

      Either.cond(
        isValidDateOrEmpty,
        if (dateValue.isEmpty) None
        else
          Some(
            formatterDateTimeWithoutSeconds
              .parseDateTime(dateValue)
              .toSharedDateTime()),
        BadDateAndNonEmptyFormat(tag, dateValue)
      )
    }

    def validateTime(tag: String, customDateFormat: CustomDateFormat)
      : Either[DomainValidation, SharedTimeOnly] = {
      val dateValue = tagText(tag)

      Either.cond(
        validationFuncForDateFormat(customDateFormat)(dateValue),
        formatterForDateFormat(customDateFormat)
          .parseDateTime(dateValue)
          .toSharedTimeOnly(),
        BadTimeFormat(tag, dateValue)
      )
    }

    def validateTimeOrEmpty(tag: String): Either[DomainValidation, Option[SharedTimeOnly]] = {
      val dateValue = tagText(tag)

      val isValidDateOrEmpty = dateValue.matches("""^\d{2}\:\d{2}$""") || dateValue.isEmpty

      Either.cond(
        isValidDateOrEmpty,
        if (dateValue.isEmpty) None
        else
          Some(
            formatterTimeWithoutSeconds
              .parseDateTime(dateValue)
              .toSharedTimeOnly()
          ),
        BadDateAndNonEmptyFormat(tag, dateValue)
      )
    }

    def validateTimeOrEmpty(tag: String, customDateFormat: CustomDateFormat)
    : Either[DomainValidation, Option[SharedTimeOnly]] = {
      val dateValue = tagText(tag)

      val isValidDateOrEmpty = validationFuncForDateFormat(customDateFormat)(dateValue) || dateValue.isEmpty

      Either.cond(
        isValidDateOrEmpty,
        if(dateValue.isEmpty) None
        else Some(formatterForDateFormat(customDateFormat)
            .parseDateTime(dateValue)
            .toSharedTimeOnly()),
        BadTimeFormat(tag, dateValue)
      )
    }

    def validateNonEmpty(
        tag: String,
        customMsg: String = ""): Either[DomainValidation, String] = {
      val fieldValue = tagText(tag)

      Either.cond(
        fieldValue.nonEmpty,
        fieldValue,
        EmptyField(tag, customMsg)
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

    def validateIntOrEmpty(
        tag: String): Either[DomainValidation, Option[Int]] = {
      val fieldValue = tagText(tag)

      if (fieldValue.isEmpty)
        Right(None)
      else if (Try(fieldValue.toInt).isFailure)
        Left(FieldIsNotAnInt(tag))
      else
        Right(Some(fieldValue.toInt))

    }

    def stringOrNone(tag: String): Option[String] = {
      val fieldValue = tagText(tag)

      if (fieldValue.nonEmpty)
        Some(fieldValue)
      else
        None

    }
  }

  def fetchLogIfErrorAndSaveWithSleep[T](
      insertF: Seq[T] => Unit,
      fetchF: () => Either[FileOrConnectivityError,
                           Seq[Either[DomainValidation, T]]]) = {
    fetchF() match {
      case Right(list) =>
        insertF(list.collect {
          case Right(value) => value
        })

        print(".")
        Thread.sleep(201)

        list.collect {
          case Left(err) => logger.warn(err.errorMessage)
        }
      case Left(err) => logger.error(err.errorMessage)

    }
  }
}
