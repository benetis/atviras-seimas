package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.shared._
import scala.xml._
import cats.implicits._
import me.benetis.coordinator.repository.{SessionRepo, TermOfOfficeRepo}

object SessionDownloader extends LazyLogging {
  def fetchAndSave() = {
    fetchLogIfErrorAndSaveWithSleep(SessionRepo.insert, () => fetch())
  }

  private def fetch(): Either[FileOrConnectivityError,
                              Seq[Either[DomainValidation, Session]]] = {
    val uri = uri"http://apps.lrs.lt/sip/p2b.ad_seimo_sesijos?ar_visos=T"
    val request =
      sttp.get(uri)

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) => Right(parse(scala.xml.XML.loadString(body)))
      case Left(err)   => Left(CannotReachWebsite(uri.toString(), err))

    }
  }

  private def parse(body: Elem): Seq[Either[DomainValidation, Session]] = {
    val termsOfOfficeWithSessions = body \\ "SeimoKadencija"

    termsOfOfficeWithSessions.flatMap((termNode: Node) => {
      val id =
        termNode.validateInt("kadencijos_id").map(TermOfOfficeId)

      val sessions = termNode \\ "SeimoSesija"

      sessions.map(sessionNode => validate(sessionNode, id))

    })
  }

  private def validate(
      node: Node,
      termOfficeIdEith: Either[DomainValidation, TermOfOfficeId])
    : Either[DomainValidation, Session] = {

    termOfficeIdEith match {
      case Right(termId) =>
        for {
          sessionId <- node.validateInt("sesijos_id")
          number    <- node.validateNonEmpty("numeris")
          name      <- node.validateNonEmpty("pavadinimas")
          dateFrom  <- node.validateDate("data_nuo")
          dateTo    <- node.validateDateOrEmpty("data_iki")
        } yield
          Session(
            SessionId(sessionId),
            termId,
            SessionNumber(number),
            SessionName(name),
            SessionDateFrom(dateFrom),
            dateTo.map(SessionDateTo)
          )

      case Left(err) => Left(err)
    }

  }
}
