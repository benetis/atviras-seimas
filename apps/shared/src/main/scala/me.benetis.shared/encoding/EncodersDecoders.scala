package me.benetis.shared.encoding

import com.typesafe.scalalogging.LazyLogging
import me.benetis.shared._

object EncodersDecoders extends LazyLogging {

  def agendaQuestionStatus(status: String): AgendaQuestionStatus = {
    status match {
      case "Tvirtinimas"                  => Affirmation
      case "Priėmimas"                    => Adoption
      case "Svarstymas"                   => Discussion
      case "Pateikimas"                   => Presentation
      case "Grąžinto įstatymo pateikimas" => PresentationOfReturnedLawDocument
      case "Klausimas"                    => Question
      case "Interpeliacijos nagrinėjimas" => InterpolationAnalysis
      case _ =>
        logger.error(s"Not supported status '$status'")
        UnknownStatus
    }
  }

  def AgendaQuestionSpeakersSerializer(
      AgendaQuestionSpeakers: AgendaQuestionSpeakers): String = {
    AgendaQuestionSpeakers.speakers.mkString("/")
  }

  def AgendaQuestionSpeakersDeSerializer(
      str: String): AgendaQuestionSpeakers = {
    AgendaQuestionSpeakers(str.split("/").toVector)
  }

  def voteSerializer(singleVote: SingleVote): Int = {
    singleVote match {
      case SingleVoteAbstain => 0
      case SingleVoteFor     => 1
      case SingleVoteAgainst => 2
      case DidntVote         => 3
    }
  }

  def voteDeserializer(singleVoteInt: Int): SingleVote = {
    singleVoteInt match {
      case 0 => SingleVoteAbstain
      case 1 => SingleVoteFor
      case 2 => SingleVoteAgainst
      case 3 => DidntVote
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
      case Open              => 0
      case Closed            => 1
      case AgreedByConsensus => 2
      case AlternativeVoting => 3
    }
  }

}
