package me.benetis.coordinator.downloader

import cats.effect.IO
import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.{PlenaryRepo, SessionRepo}
import me.benetis.shared._
import scala.xml._

object PlenaryDownloader extends LazyLogging {
  def fetchAndSave(sessions: Vector[Session]) = {
    sessions.foreach(session => {
      fetchLogIfErrorAndSaveWithSleep(PlenaryRepo.insert,
                                      () => fetch(session.id))
    })
  }

  private def fetch(
      sessionId: SessionId): Either[FileOrConnectivityError,
                                    Seq[Either[DomainValidation, Plenary]]] = {

    val sesijos_id = sessionId.session_id
    val uri =
      uri"http://apps.lrs.lt/sip/p2b.ad_seimo_posedziai?sesijos_id=$sesijos_id"
    val request =
      sttp.get(uri)

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) =>
        Right(parse(scala.xml.XML.loadString(body), sessionId))
      case Left(err) => Left(CannotReachWebsite(uri.toString(), err))

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
