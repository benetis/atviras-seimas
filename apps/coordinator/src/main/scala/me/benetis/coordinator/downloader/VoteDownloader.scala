package me.benetis.coordinator.downloader

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.DateTimeNormal
import me.benetis.coordinator.repository.{
  FactionRepo,
  AgendaQuestionRepo,
  VoteRepo
}
import me.benetis.shared._
import scala.xml._

object VoteDownloader extends LazyLogging {
  def fetchAndSave() = {
    fetchLogIfErrorAndSaveWithSleep(VoteRepo.insert,
                                    () => fetch(VoteId(-27089)))
  }

  private def fetch(
      voteId: VoteId): Either[String, Seq[Either[DomainValidation, Vote]]] = {

    val balsavimo_id = voteId.vote_id

    val request =
      sttp.get(
        uri"http://apps.lrs.lt/sip/p2b.ad_sp_balsavimo_rezultatai?balsavimo_id=$balsavimo_id")

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send().body

    response match {
      case Right(body) =>
        Right(parse(scala.xml.XML.loadString(body), voteId))
      case Left(err) => Left(err)
    }
  }

  private def parse(body: Elem,
                    voteId: VoteId): Seq[Either[DomainValidation, Vote]] = {

    val generalVoteResultNode = body \\ "SeimoInformacija" \\ "SeimoNariųBalsavimas" \\ "BendriBalsavimoRezultatai"

    val individualVoteResultNode =
      body \\ "SeimoInformacija" \\ "SeimoNariųBalsavimas" \\ "IndividualusBalsavimoRezultatas"

    individualVoteResultNode.map(individualNode =>
      validate(generalVoteResultNode.head, individualNode, voteId))
  }

  private def votedStringToVoted(votedString: String): SingleVote = {
    votedString match {
      case "Už"        => SingleVoteFor
      case "Prieš"     => SingleVoteAgainst
      case "Susilaikė" => SingleVoteAbstain
      case ""          => DidntVote
    }
  }

  private def votePersonId(id: VoteId, i: PersonId): VotePersonId = {
    VotePersonId(s"${id.vote_id}/${i.person_id}")
  }

  private def validate(generalVoteResultNode: Node,
                       individualVoteNode: Node,
                       voteId: VoteId): Either[DomainValidation, Vote] = {
    for {
      voteTime <- generalVoteResultNode.validateDateTime("balsavimo_laikas",
                                                         DateTimeNormal)
      voteTotal      <- generalVoteResultNode.validateInt("balsavo")
      voteTotalMax   <- generalVoteResultNode.validateInt("viso")
      voteFor        <- generalVoteResultNode.validateInt("už")
      voteAgainst    <- generalVoteResultNode.validateInt("prieš")
      voteAbstain    <- generalVoteResultNode.validateInt("susilaikė")
      comment        <- Right(generalVoteResultNode.tagText("komentaras"))
      personId       <- individualVoteNode.validateInt("asmens_id")
      personName     <- individualVoteNode.validateNonEmpty("vardas")
      personLastName <- individualVoteNode.validateNonEmpty("pavardė")
      factionAcr     <- Right(individualVoteNode.tagText("frakcija"))
      vote <- Right(
        votedStringToVoted(individualVoteNode.tagText("kaip_balsavo")))

    } yield
      Vote(
        voteId,
        votePersonId(voteId, PersonId(personId)),
        VoteTime(voteTime),
        VoteTotal(voteTotal),
        VoteTotalMax(voteTotalMax),
        VoteFor(voteFor),
        VoteAgainst(voteAgainst),
        VoteAbstained(voteAbstain),
        VoteComment(comment),
        PersonId(personId),
        PersonName(personName),
        PersonSurname(personLastName),
        if (factionAcr.isEmpty) None else Some(FactionAcronym(factionAcr)),
        vote
      )

  }

}
