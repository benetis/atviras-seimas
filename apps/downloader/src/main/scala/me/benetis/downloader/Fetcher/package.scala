package me.benetis.downloader

import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import scala.util.Try
import scala.xml.Node

package object Fetcher extends LazyLogging {

  val formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm")

  implicit class nodeExt(val o: Node) extends AnyVal {
    private def tagText(tag: String): String = (o \ s"@$tag").text

    def validateDate(tag: String): Either[DomainValidation, DateTime] = {
      val dateValue = tagText(tag)
      Either.cond(
        dateValue.matches("""^\d{4}-\d{2}-\d{2}$"""),
        new DateTime(dateValue),
        BadDateFormat(tag)
      )
    }

    def validateDateOrEmpty(
        tag: String): Either[DomainValidation, Option[DateTime]] = {
      val dateValue = tagText(tag)

      val isValidDateOrEmpty = dateValue.matches("""^\d{4}-\d{2}-\d{2}$""") || dateValue.isEmpty

      Either.cond(
        isValidDateOrEmpty,
        if (dateValue.isEmpty) None else Some(new DateTime(dateValue)),
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
        else Some(formatter.parseDateTime(dateValue)),
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

  def fetchLogIfErrorAndSave[T](
      insertF: Seq[T] => Unit,
      fetchF: () => Either[String, Seq[Either[DomainValidation, T]]]) = {
    fetchF() match {
      case Right(list) =>
        insertF(list.collect {
          case Right(value) => value
        })

        list.collect {
          case Left(err) => logger.warn(err.errorMessage)
        }
      case Left(err) => logger.error(err)

    }
  }
}
