package me.benetis.shared

import io.getquill.Embedded

sealed trait SingleVote       extends Embedded
case object SingleVoteFor     extends SingleVote
case object SingleVoteAgainst extends SingleVote
case object SingleVoteAbstain extends SingleVote
case object DidntVote         extends SingleVote

sealed trait VoteType         extends Embedded
case object Open              extends VoteType
case object Closed            extends VoteType
case object AgreedByConsensus extends VoteType
case object AlternativeVoting extends VoteType

case class VoteTime(time: SharedDateTime) extends Embedded
case class VoteTotal(vote_total: Int)     extends Embedded
case class VoteTotalMax(vote_total_max: Int)
    extends Embedded
case class VoteFor(vote_for: Int)         extends Embedded
case class VoteAgainst(vote_against: Int) extends Embedded
case class VoteAbstained(vote_abstained: Int)
    extends Embedded
case class VoteComment(comment: String) extends Embedded
case class VoteId(vote_id: Int)         extends Embedded
case class VoteTermSpecificId(vote_term_specific_id: Int)
case class VotePersonId(vote_person_id: String)
    extends Embedded

case class Vote(
  id: VoteId,
  votePersonId: VotePersonId,
  time: VoteTime,
  voteTotal: VoteTotal,
  voteTotalMax: VoteTotalMax,
  voteFor: VoteFor,
  voteAgainst: VoteAgainst,
  voteAbstained: VoteAbstained,
  comment: VoteComment,
  personId: ParliamentMemberId,
  name: ParliamentMemberName,
  surname: ParliamentMemberSurname,
  faction: Option[FactionAcronym],
  vote: SingleVote)

case class VoteReduced(
  id: VoteId,
  singleVote: SingleVote,
  personId: ParliamentMemberId,
  dateTime: VoteTime,
  termSpecificVoteId: Option[VoteTermSpecificId])
