package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.{FactionRepo, SessionRepo}
import me.benetis.shared._
import scala.xml._

object FactionDownloader extends LazyLogging {
  def fetchAndSave() = {
    fetchLogIfErrorAndSaveWithSleep(FactionRepo.insert, () => fetch())
  }

  private def fetch()
    : Either[String, Seq[Either[DomainValidation, Faction]]] = {
    val request =
      sttp.get(uri"http://apps.lrs.lt/sip/p2b.ad_seimo_frakcijos")

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) => Right(parse(scala.xml.XML.loadString(body)))
      case Left(err)   => Left(err)
    }
  }

  private def parse(body: Elem): Seq[Either[DomainValidation, Faction]] = {
    val factions = body \\ "SeimoInformacija" \\ "SeimoKadencija" \\ "SeimoFrakcija"

    factions.map(validate)
  }

  private def validate(node: Node): Either[DomainValidation, Faction] = {

    for {
      factionId <- node.validateInt("padalinio_id")
      name      <- node.validateNonEmpty("padalinio_pavadinimas")
      acronym   <- node.validateNonEmpty("padalinio_pavadinimo_santrumpa")
    } yield
      Faction(
        FactionId(factionId),
        FactionName(name),
        FactionAcronym(acronym)
      )

  }
}
