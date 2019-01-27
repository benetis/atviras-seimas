package me.benetis.shared.encoding

import me.benetis.shared._

object Encoders {
  def AgendaQuestionStatusSerializer(
      AgendaQuestionStatus: AgendaQuestionStatus): Int = {
    AgendaQuestionStatus match {
      case Adoption                          => 0
      case Discussion                        => 1
      case Affirmation                       => 2
      case Presentation                      => 3
      case PresentationOfReturnedLawDocument => 4
      case Question                          => 5
      case InterpolationAnalysis             => 6
      case UnknownStatus                     => 7
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

  def discussionEventSerializer(
      discussionEventType: DiscussionEventType): Int = {
    discussionEventType match {
      case Speech       => 0
      case Registration => 1
      case Voting       => 2
    }
  }

  def voteTypeSerialize(voteType: VoteType): Int = {
    voteType match {
      case Open   => 0
      case Closed => 1
    }
  }

}
