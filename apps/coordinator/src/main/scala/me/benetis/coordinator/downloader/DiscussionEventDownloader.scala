package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.DiscussionEventRepo
import me.benetis.shared.dates.DateFormatters.CustomFormatTimeOnly
import me.benetis.shared._
import scala.xml._

object DiscussionEventDownloader extends LazyLogging {
  def fetchAndSave() = {

//    val agendaQuestionId = AgendaQuestionId(-25758)

//    fetchLogIfErrorAndSaveWithSleep(DiscussionEventRepo.insert, () => fetch())
  }

  private def fetch(agendaQuestionId: AgendaQuestionId, plenary: Plenary)
    : Either[String, Seq[Either[DomainValidation, DiscussionEvent]]] = {

    val darbotvarkes_klausimo_id = agendaQuestionId.agenda_question_id

    val request =
      sttp.get(
        uri"http://apps.lrs.lt/sip/p2b.ad_sp_klausimo_svarstymo_eiga?darbotvarkes_klausimo_id=$darbotvarkes_klausimo_id")

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) =>
        Right(parse(scala.xml.XML.loadString(body), agendaQuestionId, plenary))
      case Left(err) => Left(err)
    }
  }

  private def parse(
      body: Elem,
      agendaQuestionId: AgendaQuestionId,
      plenary: Plenary): Seq[Either[DomainValidation, DiscussionEvent]] = {
    val discussions = body \\ "SeimoInformacija" \\ "SeimoPosėdžioKlausimas" \\ "SvarstymoEigosĮvykis"

    discussions.map((discussionEvent: Node) =>
      validate(discussionEvent, agendaQuestionId, plenary))
  }

  private def voteTypeDecoder(voteType: String): VoteType = {
    voteType match {
      case "Atviras" => Open
      case "Uždaras" => Closed
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
      plenary: Plenary): Either[DomainValidation, DiscussionEvent] = {
    for {
      timeFrom       <- node.validateTime("laikas_nuo", CustomFormatTimeOnly)
      eventTypeStr   <- node.validateNonEmpty("įvykio_tipas")
      personId       <- node.validateIntOrEmpty("asmens_id")
      personFullName <- Right(node.stringOrNone("asmuo"))
      registrationId <- node.validateIntOrEmpty("registracijos_id")
      voteId         <- node.validateIntOrEmpty("balsavimo_id")
      voteType       <- Right(node.stringOrNone("balsavimo_tipas"))

    } yield
      DiscussionEvent(
        agendaQuestionId,
        DiscussionEventTimeFrom(timeFrom),
        eventTypeDecoder(eventTypeStr),
        personId.map(PersonId),
        personFullName.map(PersonFullName),
        registrationId.map(RegistrationId),
        voteId.map(VoteId),
        voteType.map(voteTypeDecoder),
        plenary.id
      )

  }

}
