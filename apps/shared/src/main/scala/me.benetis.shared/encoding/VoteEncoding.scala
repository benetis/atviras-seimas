package me.benetis.shared.encoding

import me.benetis.shared._

object VoteEncoding {

  type VoteEncodingT = SingleVote => Double

  def singleVoteEncodedE1(
    singleVote: SingleVote
  ): Double = {
    singleVote match {
      case SingleVoteFor     => 2
      case SingleVoteAgainst => -2
      case SingleVoteAbstain => -1
      case DidntVote         => 0
      case _                 => -3
    }
  }

  def singleVoteEncodedE2(
    singleVote: SingleVote
  ): Double = {
    singleVote match {
      case SingleVoteFor     => 1
      case SingleVoteAgainst => -1
      case SingleVoteAbstain => 0
      case DidntVote         => 0
    }
  }

  def singleVoteEncodedE3(
    singleVote: SingleVote
  ): Double = {
    singleVote match {
      case SingleVoteFor     => 2
      case SingleVoteAgainst => -2
      case SingleVoteAbstain => -1
      case DidntVote         => -1
    }
  }
}
