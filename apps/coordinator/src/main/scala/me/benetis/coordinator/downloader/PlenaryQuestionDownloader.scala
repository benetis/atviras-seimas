package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.{
  DiscussionEventRepo,
  PlenaryQuestionRepo
}
import me.benetis.coordinator.utils.DateFormatters.{
  CustomFormatDateTimeWithoutSeconds,
  CustomFormatTimeOnlyWithoutSeconds
}
import me.benetis.shared._
import me.benetis.shared.encoding.Decoders
import scala.xml._

object PlenaryQuestionDownloader extends LazyLogging {
  def fetchAndSave(plenaries: List[Plenary]) = {
    plenaries.map(
      plenary =>
        fetchLogIfErrorAndSaveWithSleep(
          PlenaryQuestionRepo.insert,
          () => fetch(plenary)
      ))

  }

  private def fetch(plenary: Plenary)
    : Either[FileOrConnectivityError,
             Seq[Either[DomainValidation, PlenaryQuestion]]] = {

    val posedzio_id = plenary.id.plenary_id
    val uri =
      uri"http://apps.lrs.lt/sip/p2b.ad_seimo_posedzio_eiga?posedzio_id=$posedzio_id"
    val request =
      sttp.get(uri)

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) =>
        Right(parse(scala.xml.XML.loadString(body), plenary))
      case Left(err) => Left(CannotReachWebsite(uri.toString(), err))
    }
  }

  private def parse(
      body: Elem,
      plenary: Plenary): Seq[Either[DomainValidation, PlenaryQuestion]] = {
    val plenaryQuestions = body \\ "SeimoInformacija" \\ "SeimoPosėdis" \\ "EigosKlausimas"

    plenaryQuestions.map((questionNode: Node) =>
      validate(questionNode, plenary))
  }

  private def validate(
      node: Node,
      plenary: Plenary): Either[DomainValidation, PlenaryQuestion] = {

    plenary.timeStart match {
      case Some(plenaryStart) =>
        for {
          number <- node.validateNonEmpty("numeris")
          title  <- node.validateNonEmpty("pavadinimas")
          timeFrom <- node.validateTime("laikas_nuo",
                                        CustomFormatTimeOnlyWithoutSeconds)
          status           <- Right(node.stringOrNone("stadija"))
          agendaQuestionId <- node.validateInt("svarstomo_klausimo_id")

        } yield
          PlenaryQuestion(
            AgendaQuestionId(agendaQuestionId),
            PlenaryQuestionGroupId(s"${plenary.id.plenary_id}/$number"),
            PlenaryQuestionTitle(title),
            PlenaryQuestionTimeFrom(timeFrom),
            PlenaryQuestionDateTimeFrom(
              DateUtils.timeWithDateToDateTime(timeFrom,
                                               plenaryStart.time_start)
            ),
            status.map(Decoders.agendaQuestionStatus),
            status.map(PlenaryQuestionStatusRaw),
            PlenaryQuestionNumber(number),
            plenary.id
          )

      case None => Left(PlenaryShouldBeStarted(plenary.id))
    }

  }

}
