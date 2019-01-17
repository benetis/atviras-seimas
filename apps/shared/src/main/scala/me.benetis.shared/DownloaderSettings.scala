package me.benetis.shared

import io.getquill.Embedded
import org.joda.time.DateTime

sealed trait DownloaderSettings

//Dar pagalvoti ar daryt tik naujiems duomenims, gal pradzioje nedaryt
case object FetchTermOfOffice extends DownloaderSettings
case object FetchSessions extends DownloaderSettings

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

case class PlenaryId(plenary_id: Int) extends Embedded
case class PlenaryNumber(number: String) extends Embedded
case class PlenaryType(plenary_type: String) extends Embedded
case class PlenaryTimeStart(time_start: DateTime) extends Embedded
case class PlenaryTimeFinish(time_finish: DateTime) extends Embedded
case class Plenary(id: PlenaryId,
                   sessionId: SessionId,
                   number: PlenaryNumber,
                   plenaryType: PlenaryType,
                   timeStart: Option[PlenaryTimeStart],
                   timeFinish: Option[PlenaryTimeFinish])
    extends Embedded

case class SessionId(session_id: Int) extends Embedded
case class SessionName(name: String) extends Embedded
case class SessionNumber(number: String) extends Embedded
case class SessionDateFrom(date_from: DateTime) extends Embedded
case class SessionDateTo(date_to: DateTime) extends Embedded
case class Session(id: SessionId,
                   termOfOfficeId: TermOfOfficeId,
                   number: SessionNumber,
                   name: SessionName,
                   date_from: SessionDateFrom,
                   date_to: Option[SessionDateTo])

case class TermOfOfficeId(term_of_office_id: Int) extends Embedded
case class TermOfOfficeName(name: String) extends Embedded
case class TermOfOfficeDateFrom(dateFrom: DateTime) extends Embedded
case class TermOfOfficeDateTo(dateTo: DateTime) extends Embedded
case class TermOfOffice(id: TermOfOfficeId,
                        name: TermOfOfficeName,
                        dateFrom: TermOfOfficeDateFrom,
                        dateTo: Option[TermOfOfficeDateTo])
