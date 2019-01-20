package me.benetis.coordinator.utils

import me.benetis.shared._

object Encoders {
  def plenaryQuestionStatusSerializer(
      plenaryQuestionStatus: PlenaryQuestionStatus): Int = {
    plenaryQuestionStatus match {
      case Adtoption    => 0
      case Discussion   => 1
      case Affirmation  => 2
      case Presentation => 3
    }
  }

  def plenaryQuestionSpeakersSerializer(
      plenaryQuestionSpeakers: PlenaryQuestionSpeakers): String = {
    plenaryQuestionSpeakers.speakers.mkString("/")
  }

  def voteSerializer(singleVote: SingleVote): Int = {
    singleVote match {
      case SingleVoteAbstain => 0
      case SingleVoteFor     => 1
      case SingleVoteAgainst => 2
      case DidntVote         => 3
    }
  }

}
