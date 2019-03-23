package me.benetis.coordinator.utils

sealed trait ComputingError {
  def msg(): String
}

case class DBNotExpectedResult(moreInfo: String) extends ComputingError {
  override def msg() =
    s"Database returned something that was not expected. '$moreInfo'"
}
