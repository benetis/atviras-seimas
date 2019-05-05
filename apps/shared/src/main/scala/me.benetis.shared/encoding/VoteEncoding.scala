package me.benetis.shared.encoding

import io.getquill.Embedded
import me.benetis.shared._

object VoteEncoding {

  sealed trait VoteEncodingConfig extends Embedded {
    def encode(singleVote: SingleVote): Double
  }
  case object VoteEncodingE1 extends VoteEncodingConfig {
    def encode(singleVote: SingleVote): Double = {
      singleVote match {
        case SingleVoteFor     => 2
        case SingleVoteAgainst => -2
        case SingleVoteAbstain => -1
        case DidntVote         => 0
        case _                 => -3
      }
    }
  }

  case object VoteEncodingE2 extends VoteEncodingConfig {
    def encode(singleVote: SingleVote): Double = {
      singleVote match {
        case SingleVoteFor     => 1
        case SingleVoteAgainst => -1
        case SingleVoteAbstain => 0
        case DidntVote         => 0
      }
    }
  }

  case object VoteEncodingE3 extends VoteEncodingConfig {
    def encode(singleVote: SingleVote): Double = {
      singleVote match {
        case SingleVoteFor     => 2
        case SingleVoteAgainst => -2
        case SingleVoteAbstain => -1
        case DidntVote         => -1
      }
    }
  }

  lazy val encoderMap: Map[VoteEncodingConfig, String] =
    Map(
      VoteEncodingE1 -> "E1",
      VoteEncodingE2 -> "E2",
      VoteEncodingE3 -> "E3"
    )

  def encode(
    voteEncodingConfig: VoteEncodingConfig
  ): String = {
    encoderMap.getOrElse(
      voteEncodingConfig,
      "encoding_doesnt_exist"
    )
  }

  def decode(value: String): VoteEncodingConfig = {
    encoderMap.find(_._2 == value) match {
      case Some(e) => e._1
      case None    => VoteEncodingE1
    }
  }
}
