package me.benetis.coordinator

import com.typesafe.scalalogging.LazyLogging
import me.benetis.shared.{DateTimeOnlyDate, DateTimeOnlyTime}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import scala.util.Try
import scala.xml.Node
import me.benetis.coordinator.utils.DateFormatters._

package object downloader extends LazyLogging {

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

    def validateTime(tag: String, customDateFormat: CustomDateFormat)
      : Either[DomainValidation, DateTimeOnlyTime] = {
      val dateValue = tagText(tag)

      Either.cond(
        validationFuncForDateFormat(customDateFormat)(dateValue),
        DateTimeOnlyTime(
          formatterForDateFormat(customDateFormat).parseDateTime(dateValue)),
        BadTimeFormat(tag)
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
        Thread.sleep(250)

        list.collect {
          case Left(err) => logger.warn(err.errorMessage)
        }
      case Left(err) => logger.error(err.errorMessage)

    }
  }
}
