package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import java.text.Normalizer
import me.benetis.coordinator.repository.{
  FactionRepo,
  ParliamentMemberRepo
}
import me.benetis.shared._
import scala.xml._

object ParliamentMembersDownloader extends LazyLogging {

  def fetchAndSave(
      termOfOffices: List[TermOfOffice]): Unit = {

    val factions = FactionRepo.list()

    termOfOffices.map(term => {
      fetchLogIfErrorAndSaveWithSleep(
        ParliamentMemberRepo.insert,
        () => fetch(term.id, factions))
    })

  }

  private def fetch(termOfOfficeId: TermOfOfficeId,
                    factions: Vector[Faction]): Either[
    FileOrConnectivityError,
    Seq[Either[DomainValidation, ParliamentMember]]] = {

    val kadencijos_id = termOfOfficeId.term_of_office_id

    val uri =
      uri"http://apps.lrs.lt/sip/p2b.ad_seimo_nariai?kadencijos_id=$kadencijos_id"
    val request = sttp.get(uri)

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) =>
        Right(
          parse(scala.xml.XML.loadString(body),
                termOfOfficeId,
                factions))
      case Left(err) =>
        Left(CannotReachWebsite(uri.toString(), err))

    }
  }

  private def parse(body: Elem,
                    termOfOfficeId: TermOfOfficeId,
                    factions: Vector[Faction])
    : Seq[Either[DomainValidation, ParliamentMember]] = {
    val members = body \\ "SeimoInformacija" \\ "SeimoKadencija" \\ "SeimoNarys"

    members.map(validate(_, termOfOfficeId, factions))
  }

  private def constructUniqueId(
      parliamentMemberId: ParliamentMemberId,
      termOfOfficeId: TermOfOfficeId)
    : ParliamentMemberUniqueId = {
    ParliamentMemberUniqueId(
      s"${parliamentMemberId.person_id}/${termOfOfficeId.term_of_office_id}")
  }

  private def validate(node: Node,
                       termOfOfficeId: TermOfOfficeId,
                       factions: Vector[Faction])
    : Either[DomainValidation, ParliamentMember] = {
    for {
      personId    <- node.validateInt("asmens_id")
      name        <- node.validateNonEmpty("vardas")
      surname     <- node.validateNonEmpty("pavardė")
      gender      <- node.validateNonEmpty("lytis")
      dateFrom    <- node.validateDate("data_nuo")
      dateTo      <- node.validateDateOrEmpty("data_iki")
      factionName <- Right(node.tagText("iškėlusi_partija"))
      electedHow  <- node.validateNonEmpty("išrinkimo_būdas")
      termOfOfficeAmount <- node.validateInt(
        "kadencijų_skaičius")
      biographyLink <- Right(
        node.tagText("biografijos_nuoroda"))

    } yield {

      val parliamentMemberId = ParliamentMemberId(personId)

      ParliamentMember(
        uniqueId = constructUniqueId(parliamentMemberId,
                                     termOfOfficeId),
        personId = parliamentMemberId,
        name = ParliamentMemberName(name),
        surname = ParliamentMemberSurname(surname),
        gender =
          ParliamentMemberGender(gender.head.toString),
        date_from = ParliamentMemberDateFrom(dateFrom),
        date_to = dateTo.map(ParliamentMemberDateTo),
        factionName =
          ParliamentMemberFactionName(factionName),
        factionId = factions
          .find(_.name.faction_name == factionName)
          .map(_.id)
          .getOrElse(FactionId(-1)),
        electedHow = ParliamentMemberElectedHow(electedHow),
        termOfOfficeAmount =
          ParliamentMemberTermOfOfficeAmount(
            termOfOfficeAmount),
        biographyLink =
          ParliamentMemberBiographyLink(biographyLink),
        termOfOfficeId = termOfOfficeId,
        termOfOfficeSpecificId = None
      )
    }

  }

}
