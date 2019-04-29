package me.benetis.coordinator.utils

sealed trait ComputingError {
  def msg(): String
}

case class DBNotExpectedResult(moreInfo: String)
    extends ComputingError {
  override def msg() =
    s"Database returned something that was not expected. '$moreInfo'"
}

case class LibraryNotBehavingAsExpected(error: String)
    extends ComputingError {
  override def msg(): String =
    s"Some library is doing different things than expected. Might be throwing some exceptions, here take its msg: '$error'"
}

case class CustomError(error: String)
    extends ComputingError {
  override def msg() =
    s"Custom error: '$error'"

}
