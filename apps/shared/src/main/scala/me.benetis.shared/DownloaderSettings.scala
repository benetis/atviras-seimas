package me.benetis.shared

import org.joda.time.DateTime

sealed trait DownloaderSettings

case class IndividualId(value: String)
case class IndividualName(value: String)
case class IndividualSurname(value: String)
case class IndividualFraction(value: String)

sealed trait SingleVote
case object VoteFor extends SingleVote
case object VoteAgainst extends SingleVote
case object VoteAbstain extends SingleVote
case object DidntVote extends SingleVote

case class SingleVoteResult(
    individualId: IndividualId,
    name: IndividualName,
    surname: IndividualSurname,
    fraction: IndividualFraction,
    vote: SingleVote
)

case class VoteTime(value: DateTime)
case class VoteTotal(value: Int)
case class VoteTotalMax(value: Int)
case class VoteFor(value: Int)
case class VoteAgainst(value: Int)
case class VoteAbstained(value: Int)
case class VoteComment(value: String)
case class VoteId(value: String)

case class VoteResults(voteId: VoteId,
                       voteTime: VoteTime,
                       voteTotal: VoteTotal,
                       voteTotalMax: VoteTotalMax,
                       voteFor: VoteFor,
                       voteAgainst: VoteAgainst,
                       voteAbstained: VoteAbstained,
                       voteComment: VoteComment)

case class PlenaryQuestionId(value: String)
case class PlenaryQuestionTitle(value: String)
case class PlenaryQuestionTimeFrom(value: DateTime)
case class PlenaryQuestionNumber(value: String)

sealed trait PlenaryQuestionStatus
case object Admission extends PlenaryQuestionStatus
case object Discussion extends PlenaryQuestionStatus
case object Approval extends PlenaryQuestionStatus

case class PlenaryQuestion(id: PlenaryQuestionId,
                           title: PlenaryQuestionTitle,
                           timeFrom: PlenaryQuestionTimeFrom,
                           number: PlenaryQuestionNumber,
                           status: PlenaryQuestionStatus)

case class PlenaryId(value: String)
case class Plenary(id: PlenaryId, sessionId: SessionId)

case class SessionId(value: String)
case class SessionName(value: String)
case class SessionNumber(value: String)
case class SessionTimeFrom(value: DateTime)
case class SessionTimeTo(value: DateTime)
case class Session(id: SessionId,
                   termOfOfficeId: TermOfOfficeId,
                   number: SessionNumber,
                   name: SessionName,
                   from: SessionTimeFrom,
                   to: SessionTimeTo)

case class TermOfOfficeId(value: String)
case class TermOfOffice(id: TermOfOfficeId)
