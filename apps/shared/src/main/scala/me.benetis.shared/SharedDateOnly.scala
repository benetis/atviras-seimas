package me.benetis.shared

case class SharedDateOnly(millis: Long) {
  def >(sharedDateOnly: SharedDateOnly): Boolean = {
    millis > sharedDateOnly.millis
  }

  def <=(sharedDateOnly: SharedDateOnly): Boolean = {
    millis <= sharedDateOnly.millis
  }
}
