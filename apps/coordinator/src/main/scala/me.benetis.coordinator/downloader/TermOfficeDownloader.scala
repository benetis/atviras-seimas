package me.benetis.coordinator.downloader
import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.shared._
import scala.xml._
import cats.implicits._
import me.benetis.coordinator.repository.TermOfOfficeRepo

object TermOfficeDownloader {

  def fetchAndSave(): Unit = {
    fetchLogIfErrorAndSaveWithSleep(TermOfOfficeRepo.insert, () => fetch())
  }

  private def fetch(): Either[FileOrConnectivityError,
                              Seq[Either[DomainValidation, TermOfOffice]]] = {
    val uri     = uri"http://apps.lrs.lt/sip/p2b.ad_seimo_kadencijos"
    val request = sttp.get(uri)

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) => Right(parse(scala.xml.XML.loadString(body)))
      case Left(err)   => Left(CannotReachWebsite(uri.toString(), err))

    }
  }

  private def parse(body: Elem): Seq[Either[DomainValidation, TermOfOffice]] = {
    val termsOfOffice = body \\ "SeimoKadencija"

    termsOfOffice.map(validate)
  }

  private def validate(node: Node): Either[DomainValidation, TermOfOffice] = {
    for {
      id       <- node.validateInt("kadencijos_id")
      name     <- node.validateNonEmpty("pavadinimas")
      dateFrom <- node.validateDate("data_nuo")
      dateTo   <- node.validateDateOrEmpty("data_iki")
    } yield
      TermOfOffice(
        TermOfOfficeId(id),
        TermOfOfficeName(name),
        TermOfOfficeDateFrom(dateFrom),
        dateTo.map(TermOfOfficeDateTo)
      )
  }

}
