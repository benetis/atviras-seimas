package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.coordinator.repository.MultiFactionItemRepo.ctx
import me.benetis.coordinator.repository.VoteRepo.isDateInTermOfficeRange
import me.benetis.coordinator.utils.dates.SharedDateDecoders._
import me.benetis.coordinator.utils.{
  ComputingError,
  DBNotExpectedResult
}
import me.benetis.shared._
import me.benetis.shared.encoding.{
  EncodersDecoders,
  MultiFactionItemEncodingDecoding
}
import me.benetis.shared._

object MultiFactionItemRepo {

  private lazy val ctx =
    new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  implicit val encodeSingleVotes =
    MappedEncoding[SingleVote, Int](
      EncodersDecoders.voteSerializer
    )

  implicit val decodeSingleVotes =
    MappedEncoding[Int, SingleVote](
      EncodersDecoders.voteDeserializer
    )

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._
  val factions = FactionRepo.list()

  private implicit val encodeMultiItemList =
    MappedEncoding[MultiFactionItemFactionsList, String](
      MultiFactionItemEncodingDecoding.encode
    )

  private implicit val decodemultiItemList =
    MappedEncoding[String, Option[
      MultiFactionItemFactionsList
    ]](
      v =>
        MultiFactionItemEncodingDecoding.decode(v, factions)
    )

  private implicit val FactionInsertMeta =
    insertMeta[MultiFactionItem]()

  def byTermOfOffice(
    termOfOfficeId: TermOfOfficeId
  ): List[MultiFactionItem] = {
    val q = quote {
      for {
        p <- query[MultiFactionItem].filter(
          _.termOfOfficeId.term_of_office_id == lift(
            termOfOfficeId.term_of_office_id
          )
        )
      } yield {
        p
      }
    }

    ctx.run(q)
  }

  private def findAllChangedFactions(
    personId: ParliamentMemberId,
    termOfOffice: TermOfOffice,
    factions: Vector[Faction]
  ): Vector[Faction] = {
    val q = quote {
      for {
        p <- query[Vote]
          .filter(
            _.personId.person_id == lift(
              personId.person_id
            )
          )
          .distinct
      } yield {
        p
      }
    }

    ctx
      .run(q)
      .filter(_.faction.nonEmpty)
      .map(v => (v.faction, v.time))
      .filter(
        v => isDateInTermOfficeRange(v._2, termOfOffice)
      )
      .flatMap(_._1)
      .flatMap(acr => factions.find(f => f.acronym == acr))
      .toVector
      .distinct
  }

  def addFactionsToCurrentList(
    termOfOffice: TermOfOffice
  ): Unit = {
    val q = quote {
      for {
        p <- query[MultiFactionItem].filter(
          _.termOfOfficeId.term_of_office_id == lift(
            termOfOffice.id.term_of_office_id
          )
        )
      } yield {
        p
      }
    }

    val list: List[MultiFactionItem] = ctx.run(q)

    val allChangedFactions
      : List[MultiFactionItemFactionsListWithPersonId] =
      list
        .map(
          item =>
            MultiFactionItemFactionsListWithPersonId(
              item.personId,
              MultiFactionItemFactionsList(
                findAllChangedFactions(
                  item.personId,
                  termOfOffice,
                  factions
                )
              )
            )
        )

    val a = quote {
      liftQuery(allChangedFactions).foreach { person =>
        query[MultiFactionItem]
          .filter(
            _.personId.person_id == person.personId.person_id
          )
          .update(
            _.factionsList -> Some(
              person.factionItemFactionsList
            )
          )
      }
    }

    ctx.run(a)

  }

}
