package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.PlenaryQuestionRepo
import me.benetis.shared._
import scala.xml._

object PlenaryQuestionDownloader extends LazyLogging {
  def fetchAndSave() = {
    fetchLogIfErrorAndSaveWithSleep(PlenaryQuestionRepo.insert,
                                    () => fetch(PlenaryId(-501109)))
  }

  private def fetch(plenaryId: PlenaryId)
    : Either[String, Seq[Either[DomainValidation, PlenaryQuestion]]] = {

    val posedzio_id = plenaryId.plenary_id

    val request =
      sttp.get(
        uri"http://apps.lrs.lt/sip/p2b.ad_seimo_posedzio_eiga?posedzio_id=$posedzio_id")

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) =>
        Right(parse(scala.xml.XML.loadString(body), plenaryId))
      case Left(err) => Left(err)
    }
  }

  private def parse(
      body: Elem,
      plenaryId: PlenaryId): Seq[Either[DomainValidation, PlenaryQuestion]] = {
    val plenaryQuestions = body \\ "SeimoInformacija" \\ "SeimoPosėdis" \\ "EigosKlausimas"

    plenaryQuestions.map((questionNode: Node) =>
      validate(questionNode, plenaryId))
  }

  private def statusDecoder(status: String): PlenaryQuestionStatus = {
    status match {
      case "Tvirtinimas" => PlenaryQuestionAffirmation
      case "Priėmimas"   => PlenaryQuestionAdoption
      case "Svarstymas"  => PlenaryQuestionDiscussion
      case "Pateikimas"  => PlenaryQuestionPresentation
      case _ =>
        logger.error(s"Not supported status for plenary question '$status'")
        PlenaryQuestionPresentation
    }
  }

  private def validate(
      node: Node,
      plenaryId: PlenaryId): Either[DomainValidation, PlenaryQuestion] = {
    for {
      number           <- node.validateNonEmpty("numeris")
      title            <- node.validateNonEmpty("pavadinimas")
      timeFrom         <- node.validateTime("laikas_nuo")
      status           <- node.validateNonEmpty("stadija")
      agendaQuestionId <- node.validateInt("svarstomo_klausimo_id")

    } yield
      PlenaryQuestion(
        AgendaQuestionId(agendaQuestionId),
        PlenaryQuestionGroupId(s"${plenaryId.plenary_id}/$number"),
        PlenaryQuestionTitle(title),
        PlenaryQuestionTimeFrom(timeFrom),
        statusDecoder(status),
        PlenaryQuestionNumber(number)
      )
  }

}
