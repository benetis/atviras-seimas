package me.benetis.shared

import io.getquill.Embedded
import org.joda.time.DateTime

sealed trait SingleVote       extends Embedded
case object SingleVoteFor     extends SingleVote
case object SingleVoteAgainst extends SingleVote
case object SingleVoteAbstain extends SingleVote
case object DidntVote         extends SingleVote

case class VoteTime(time: DateTime)             extends Embedded
case class VoteTotal(vote_total: Int)           extends Embedded
case class VoteTotalMax(vote_total_max: Int)    extends Embedded
case class VoteFor(vote_for: Int)               extends Embedded
case class VoteAgainst(vote_against: Int)       extends Embedded
case class VoteAbstained(vote_abstained: Int)   extends Embedded
case class VoteComment(comment: String)         extends Embedded
case class VoteId(vote_id: Int)                 extends Embedded
case class VotePersonId(vote_person_id: String) extends Embedded

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
    personId: PersonId,
    name: PersonName,
    surname: PersonSurname,
    faction: Option[FactionAcronym],
    vote: SingleVote
)
