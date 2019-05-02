package me.benetis.coordinator.downloader

import me.benetis.shared.PlenaryId
import org.joda.time.DateTime

sealed trait DomainValidation {
  def errorMessage: String
}

case class BadDateFormat(field: String)
    extends DomainValidation {
  override def errorMessage: String =
    s"Field '$field' date format is invalid"
}

case class BadDateAndNonEmptyFormat(
  field: String,
  value: String)
    extends DomainValidation {
  override def errorMessage: String =
    s"Field $field date format is invalid and field is not empty. Given centroids: '$value'"
}

case class BadTimeFormat(
  field: String,
  value: String)
    extends DomainValidation {
  override def errorMessage: String =
    s"Field $field timeString format is invalid. Given centroids: '$value'"
}

case class EmptyField(
  field: String,
  customMsg: String = "")
    extends DomainValidation {

  val msgToAdd =
    if (customMsg.nonEmpty)
      s". Custom message: '$customMsg'"
    else ""

  override def errorMessage: String =
    s"Field '$field' must be non empty $msgToAdd"
}

case class FieldIsNotAnInt(field: String)
    extends DomainValidation {
  override def errorMessage: String =
    s"Field '$field' cannot be converted to int"
}

case class PlenaryShouldBeStarted(plenaryId: PlenaryId)
    extends DomainValidation {
  override def errorMessage: String =
    s"Plenary '${plenaryId.plenary_id}' must have start timeString for it to have agenda questions"
}

sealed trait FileOrConnectivityError {
  def errorMessage: String
}

case class BadXML(
  link: String,
  error: String)
    extends FileOrConnectivityError {
  override def errorMessage: String =
    s"XML cannot be parsed as it is bad '$link', error: '$error'"
}

case class CannotReachWebsite(
  link: String,
  error: String)
    extends FileOrConnectivityError {
  override def errorMessage: String =
    s"Link '$link' cannot be reached. ComputingError: '$error'"
}
