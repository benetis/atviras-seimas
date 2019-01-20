package me.benetis.coordinator.downloader

import org.joda.time.DateTime

sealed trait DomainValidation {
  def errorMessage: String
}

case class BadDateFormat(field: String) extends DomainValidation {
  override def errorMessage: String = s"Field '$field' date format is invalid"
}

case class BadDateAndNonEmptyFormat(field: String) extends DomainValidation {
  override def errorMessage: String =
    s"Field $field date format is invalid and field is not empty"
}

case class EmptyField(field: String) extends DomainValidation {
  override def errorMessage: String = s"Field '$field' must be non empty"
}

case class FieldIsNotAnInt(field: String) extends DomainValidation {
  override def errorMessage: String =
    s"Field '$field' cannot be converted to int"
}
