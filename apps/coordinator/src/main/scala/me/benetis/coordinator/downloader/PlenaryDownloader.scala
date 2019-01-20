package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.shared.Repository.{PlenaryRepo, SessionRepo}
import me.benetis.shared._
import scala.xml._

object PlenaryDownloader extends LazyLogging {
  def fetchAndSave() = {
    fetchLogIfErrorAndSave(PlenaryRepo.insert, () => fetch(SessionId(107)))
  }

  private def fetch(sessionId: SessionId)
    : Either[String, Seq[Either[DomainValidation, Plenary]]] = {

    val sesijos_id = sessionId.session_id

    val request =
      sttp.get(
        uri"http://apps.lrs.lt/sip/p2b.ad_seimo_posedziai?sesijos_id=$sesijos_id")

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) =>
        Right(parse(scala.xml.XML.loadString(body), sessionId))
      case Left(err) => Left(err)
    }
  }

  private def parse(
      body: Elem,
      sessionId: SessionId): Seq[Either[DomainValidation, Plenary]] = {
    val plenaries = body \\ "SeimoInformacija" \\ "SeimoSesija" \\ "SeimoPosėdis"

    plenaries.map((plenaryNode: Node) => validate(plenaryNode, sessionId))
  }

  private def validate(
      node: Node,
      sessionId: SessionId): Either[DomainValidation, Plenary] = {

    for {
      plenaryId   <- node.validateInt("posėdžio_id")
      number      <- node.validateNonEmpty("numeris")
      plenaryType <- node.validateNonEmpty("tipas")
      dateStart   <- node.validateDateTimeOrEmpty("pradžia")
      dateFinish  <- node.validateDateTimeOrEmpty("pabaiga")
    } yield
      Plenary(
        PlenaryId(plenaryId),
        sessionId,
        PlenaryNumber(number),
        PlenaryType(plenaryType),
        dateStart.map(PlenaryTimeStart),
        dateFinish.map(PlenaryTimeFinish),
      )

  }
}
