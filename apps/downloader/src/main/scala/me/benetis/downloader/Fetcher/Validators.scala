package me.benetis.downloader.Fetcher

import org.joda.time.DateTime

sealed trait DomainValidation {
  def errorMessage: String
}

case object BadDateFormat extends DomainValidation {
  override def errorMessage: String = "Given date format is invalid"
}

case object BadDateAndNonEmptyFormat extends DomainValidation {
  override def errorMessage: String =
    "Given date format is invalid and field is not empty"
}

case object EmptyField extends DomainValidation {
  override def errorMessage: String = "Given field must be non empty"
}

object Validators {
  def validateDate(date: String): Either[DomainValidation, DateTime] =
    Either.cond(
      date.matches("""^\d{4}-\d{2}-\d{2}$"""),
      new DateTime(date),
      BadDateFormat
    )

  def validateDateOrEmpty(
      date: String): Either[DomainValidation, Option[DateTime]] = {
    val isValidDateOrEmpty = date.matches("""^\d{4}-\d{2}-\d{2}$""") || date.isEmpty

    Either.cond(
      isValidDateOrEmpty,
      if (date.isEmpty) None else Some(new DateTime(date)),
      BadDateAndNonEmptyFormat
    )
  }

  def validateNonEmpty(field: String): Either[DomainValidation, String] =
    Either.cond(
      field.nonEmpty,
      field,
      EmptyField
    )
}
