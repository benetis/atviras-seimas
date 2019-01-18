package me.benetis.shared

object Encoders {
  def plenaryQuestionStatusSerializer(
      plenaryQuestionStatus: PlenaryQuestionStatus): Int = {
    plenaryQuestionStatus match {
      case Admission    => 0
      case Discussion   => 1
      case Affirmation  => 2
      case Presentation => 3
    }
  }

  def plenaryQuestionSpeakersSerializer(
      plenaryQuestionSpeakers: PlenaryQuestionSpeakers): String = {
    plenaryQuestionSpeakers.speakers.mkString("/")
  }

}
