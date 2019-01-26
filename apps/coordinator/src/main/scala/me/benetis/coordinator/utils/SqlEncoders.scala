package me.benetis.coordinator.utils

import me.benetis.shared._

object SqlEncoders {
  def AgendaQuestionStatusSerializer(
      AgendaQuestionStatus: AgendaQuestionStatus): Int = {
    AgendaQuestionStatus match {
      case Adoption     => 0
      case Discussion   => 1
      case Affirmation  => 2
      case Presentation => 3
    }
  }

  def AgendaQuestionSpeakersSerializer(
      AgendaQuestionSpeakers: AgendaQuestionSpeakers): String = {
    AgendaQuestionSpeakers.speakers.mkString("/")
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
