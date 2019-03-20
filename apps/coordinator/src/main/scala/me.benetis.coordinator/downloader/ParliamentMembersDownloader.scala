package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import java.text.Normalizer
import me.benetis.coordinator.repository.ParliamentMemberRepo
import me.benetis.shared._
import scala.xml._

object ParliamentMembersDownloader extends LazyLogging {

  def fetchAndSave(termOfOffices: List[TermOfOffice]): Unit = {
    termOfOffices.map(term => {
      fetchLogIfErrorAndSaveWithSleep(ParliamentMemberRepo.insert,
                                      () => fetch(term.id))
    })

  }

  private def fetch(termOfOfficeId: TermOfOfficeId)
    : Either[FileOrConnectivityError,
             Seq[Either[DomainValidation, ParliamentMember]]] = {

    val kadencijos_id = termOfOfficeId.term_of_office_id

    val uri =
      uri"http://apps.lrs.lt/sip/p2b.ad_seimo_nariai?kadencijos_id=$kadencijos_id"
    val request = sttp.get(uri)

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) =>
        Right(parse(scala.xml.XML.loadString(body), termOfOfficeId))
      case Left(err) => Left(CannotReachWebsite(uri.toString(), err))

    }
  }

  private def parse(body: Elem, termOfOfficeId: TermOfOfficeId)
    : Seq[Either[DomainValidation, ParliamentMember]] = {
    val members = body \\ "SeimoInformacija" \\ "SeimoKadencija" \\ "SeimoNarys"

    members.map(validate(_, termOfOfficeId))
  }

  private def constructUniqueId(
      parliamentMemberId: ParliamentMemberId,
      termOfOfficeId: TermOfOfficeId): ParliamentMemberUniqueId = {
    ParliamentMemberUniqueId(
      s"${parliamentMemberId.person_id}/${termOfOfficeId.term_of_office_id}")
  }

  private def validate(node: Node, termOfOfficeId: TermOfOfficeId)
    : Either[DomainValidation, ParliamentMember] = {
    for {
      personId           <- node.validateInt("asmens_id")
      name               <- node.validateNonEmpty("vardas")
      surname            <- node.validateNonEmpty("pavardė")
      gender             <- node.validateNonEmpty("lytis")
      dateFrom           <- node.validateDate("data_nuo")
      dateTo             <- node.validateDateOrEmpty("data_iki")
      factionName        <- Right(node.tagText("iškėlusi_partija"))
      electedHow         <- node.validateNonEmpty("išrinkimo_būdas")
      termOfOfficeAmount <- node.validateInt("kadencijų_skaičius")
      biographyLink      <- Right(node.tagText("biografijos_nuoroda"))

    } yield {

      val parliamentMemberId = ParliamentMemberId(personId)

      ParliamentMember(
        constructUniqueId(parliamentMemberId, termOfOfficeId),
        parliamentMemberId,
        ParliamentMemberName(name),
        ParliamentMemberSurname(surname),
        ParliamentMemberGender(gender.head.toString),
        ParliamentMemberDateFrom(dateFrom),
        dateTo.map(ParliamentMemberDateTo),
        ParliamentMemberFactionName(factionName),
        ParliamentMemberElectedHow(electedHow),
        ParliamentMemberTermOfOfficeAmount(termOfOfficeAmount),
        ParliamentMemberBiographyLink(biographyLink)
      )
    }

  }

}
