package me.benetis.downloader.Fetcher
import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.shared._
import scala.xml._
import cats.implicits._
import me.benetis.shared.Repository.TermOfOfficeRepo

object TermOfficeDownloader extends LazyLogging {

  def fetchAndSave(): Unit = {
    val data = fetch()

    data match {
      case Right(list) =>
        TermOfOfficeRepo.insert(list.collect {
          case Right(value) => value
        })

        list.collect {
          case Left(err) => logger.warn(err.errorMessage)
        }
      case Left(err) => logger.error(err)

    }

  }

  private def fetch()
    : Either[String, Seq[Either[DomainValidation, TermOfOffice]]] = {
    val request = sttp.get(uri"http://apps.lrs.lt/sip/p2b.ad_seimo_kadencijos")

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) => Right(parse(scala.xml.XML.loadString(body)))
      case Left(err)   => Left(err)
    }
  }

  private def parse(body: Elem): Seq[Either[DomainValidation, TermOfOffice]] = {
    val termsOfOffice = body \\ "SeimoKadencija"

    termsOfOffice.map(validate)
  }

  private def validate(node: Node): Either[DomainValidation, TermOfOffice] = {
    for {
      id <- Validators.validateNonEmpty(node.tagText("kadencijos_id"))
      name <- Validators.validateNonEmpty(node.tagText("pavadinimas"))
      dateFrom <- Validators.validateDate(node.tagText("data_nuo"))
      dateTo <- Validators.validateDateOrEmpty(node.tagText("data_iki"))
    } yield
      TermOfOffice(
        TermOfOfficeId(id),
        TermOfOfficeName(name),
        TermOfOfficeDateFrom(dateFrom),
        dateTo.map(TermOfOfficeDateTo)
      )
  }

}
