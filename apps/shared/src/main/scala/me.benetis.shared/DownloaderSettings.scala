package me.benetis.shared

import io.getquill.Embedded
import org.joda.time.DateTime

sealed trait DownloaderSettings

//Dar pagalvoti ar daryt tik naujiems duomenims, gal pradzioje nedaryt
case object FetchTermOfOffice extends DownloaderSettings
case object FetchSessions extends DownloaderSettings

case class FactionId(faction_id: Int) extends Embedded
case class FactionName(name: String) extends Embedded
case class FactionAcronym(faction_acronym: String) extends Embedded
case class Faction(id: FactionId, name: FactionName, acronym: FactionAcronym)

case class PersonId(person_id: Int) extends Embedded
case class PersonName(person_name: String) extends Embedded
case class PersonSurname(person_surname: String) extends Embedded

sealed trait SingleVote extends Embedded
case object SingleVoteFor extends SingleVote
case object SingleVoteAgainst extends SingleVote
case object SingleVoteAbstain extends SingleVote
case object DidntVote extends SingleVote

case class VoteTime(time: DateTime) extends Embedded
case class VoteTotal(vote_total: Int) extends Embedded
case class VoteTotalMax(vote_total_max: Int) extends Embedded
case class VoteFor(vote_for: Int) extends Embedded
case class VoteAgainst(vote_against: Int) extends Embedded
case class VoteAbstained(vote_abstained: Int) extends Embedded
case class VoteComment(comment: String) extends Embedded
case class VoteId(vote_id: Int) extends Embedded

case class Vote(
    id: VoteId,
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
    faction: FactionAcronym,
    vote: SingleVote
)

case class PlenaryQuestionId(plenary_question_id: Int) extends Embedded
case class PlenaryQuestionGroupId(plenary_question_group_id: String)
    extends Embedded
case class PlenaryQuestionTitle(title: String) extends Embedded
case class PlenaryQuestionTimeFrom(time_from: DateTime) extends Embedded
case class PlenaryQuestionTimeTo(time_to: DateTime) extends Embedded
case class PlenaryQuestionNumber(number: String) extends Embedded
case class PlenaryQuestionDocumentLink(document_link: String) extends Embedded
case class PlenaryQuestionSpeakers(speakers: Vector[String]) extends Embedded

sealed trait PlenaryQuestionStatus extends Embedded
case object Adtoption extends PlenaryQuestionStatus
case object Discussion extends PlenaryQuestionStatus
case object Affirmation extends PlenaryQuestionStatus
case object Presentation extends PlenaryQuestionStatus

case class PlenaryQuestion(id: PlenaryQuestionId,
                           groupId: PlenaryQuestionGroupId,
                           title: PlenaryQuestionTitle,
                           timeFrom: Option[PlenaryQuestionTimeFrom],
                           timeTo: Option[PlenaryQuestionTimeTo],
                           status: PlenaryQuestionStatus,
                           documentLink: PlenaryQuestionDocumentLink,
                           speakers: PlenaryQuestionSpeakers,
                           number: PlenaryQuestionNumber)

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
