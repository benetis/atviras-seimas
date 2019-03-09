package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.{AgendaQuestionRepo, DiscussionEventRepo}
import me.benetis.coordinator.utils.dates.DateFormatters.CustomFormatTimeOnly
import me.benetis.shared._
import scala.xml._

object DiscussionEventDownloader extends LazyLogging {
  def fetchAndSave(agendaQuestions: List[AgendaQuestion]) = {
    agendaQuestions.map(
      question =>
        fetchLogIfErrorAndSaveWithSleep(
          DiscussionEventRepo.insert,
          () => fetch(question)
        ))
  }

  private def fetch(agendaQuestion: AgendaQuestion)
    : Either[FileOrConnectivityError, Seq[Either[DomainValidation, DiscussionEvent]]] = {

    val darbotvarkes_klausimo_id = agendaQuestion.id.agenda_question_id
    val uri = uri"http://apps.lrs.lt/sip/p2b.ad_sp_klausimo_svarstymo_eiga?darbotvarkes_klausimo_id=$darbotvarkes_klausimo_id"
    val request = sttp.get(uri)

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) =>
        Right(parse(scala.xml.XML.loadString(body), agendaQuestion))
      case Left(err) => Left(CannotReachWebsite(uri.toString(), err))
    }
  }

  private def parse(
      body: Elem,
      agendaQuestion: AgendaQuestion): Seq[Either[DomainValidation, DiscussionEvent]] = {
    val discussions = body \\ "SeimoInformacija" \\ "SeimoPosėdžioKlausimas" \\ "SvarstymoEigosĮvykis"

    discussions.map((discussionEvent: Node) =>
      validate(discussionEvent, agendaQuestion.id, agendaQuestion.plenaryId))
  }

  private def voteTypeDecoder(voteType: String): VoteType = {
    voteType match {
      case "Atviras" => Open
      case "Uždaras" => Closed
      case "Pritarta bendru sutarimu" => AgreedByConsensus
      case "Alternatyvus balsavimas" => AlternativeVoting
      case _ =>
        logger.error(s"Not supported vote type '$voteType'")
        Open
    }
  }

  private def eventTypeDecoder(eventType: String): DiscussionEventType = {
    eventType match {
      case "Kalba"        => Speech
      case "Registracija" => Registration
      case "Balsavimas"   => Voting
      case _ =>
        logger.error(
          s"Not supported status for discussion event type '$eventType'")
        Speech
    }
  }

  private def validate(
      node: Node,
      agendaQuestionId: AgendaQuestionId,
      plenaryId: PlenaryId): Either[DomainValidation, DiscussionEvent] = {
    for {
      timeFrom       <- node.validateTimeOrEmpty("laikas_nuo", CustomFormatTimeOnly)
      eventTypeStr   <- node.validateNonEmpty("įvykio_tipas")
      personId       <- node.validateIntOrEmpty("asmens_id")
      personFullName <- Right(node.stringOrNone("asmuo"))
      registrationId <- node.validateIntOrEmpty("registracijos_id")
      voteId         <- node.validateIntOrEmpty("balsavimo_id")
      voteType       <- Right(node.stringOrNone("balsavimo_tipas"))

    } yield
      DiscussionEvent(
        agendaQuestionId,
        timeFrom.map(DiscussionEventTimeFrom),
        eventTypeDecoder(eventTypeStr),
        personId.map(PersonId),
        personFullName.map(PersonFullName),
        registrationId.map(RegistrationId),
        voteId.map(VoteId),
        voteType.map(voteTypeDecoder),
        plenaryId
      )

  }

}
