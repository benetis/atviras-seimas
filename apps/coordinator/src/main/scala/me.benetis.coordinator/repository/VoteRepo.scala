package me.benetis.coordinator.repository

import org.joda.time.DateTime
import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.coordinator.repository.DiscussionEventRepo.ctx
import me.benetis.coordinator.repository.VoteRepo.ctx
import me.benetis.coordinator.utils.{
  ComputingError,
  DBNotExpectedResult
}
import me.benetis.shared.encoding.EncodersDecoders
import me.benetis.shared._
import me.benetis.coordinator.utils.dates.SharedDateEncoders._
import me.benetis.coordinator.utils.dates.SharedDateDecoders._
object VoteRepo {

  private lazy val ctx =
    new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

  private implicit val encodeSingleVote =
    MappedEncoding[SingleVote, Int](
      EncodersDecoders.voteSerializer
    )

  private implicit val decodeSingleVote =
    MappedEncoding[Int, SingleVote](
      EncodersDecoders.voteDeserializer
    )

  private implicit val FactionInsertMeta =
    insertMeta[Vote]()

  def insert(votes: Seq[Vote]): Unit = {
    val q = quote {
      liftQuery(votes).foreach(
        e =>
          query[Vote]
            .insert(e)
            .onConflictIgnore(_.personId)
      )
    }
    ctx.run(q)
  }

  def list(): List[VoteReduced] = {
    val q = quote {
      for {
        p <- query[Vote]
      } yield {
        p
      }
    }

    ctx.run(q).map(toVoteReduced)
  }

  def listForTermOfOffice(
    termOfOfficeId: TermOfOfficeId
  ): Either[ComputingError, List[VoteReduced]] = {

    val term = TermOfOfficeRepo.byId(termOfOfficeId)

    term match {
      case Some(termValue) =>
        Right(
          list().filter(
            vote =>
              isDateInTermOfficeRange(
                vote.dateTime,
                termValue
              )
          )
        )
      case None =>
        Left(
          DBNotExpectedResult(
            s"TermOfOffice with this ID should have been found in the DB. Id: ${termOfOfficeId.term_of_office_id}"
          )
        )
    }
  }

  private def isDateInRange(
    voteTime: VoteTime,
    dateFrom: SharedDateTime,
    dateTo: Option[SharedDateTime]
  ): Boolean = {
    dateTo match {
      case Some(dateToV) =>
        voteTime.time.millis < dateToV.millis && voteTime.time.millis > dateFrom.millis
      case None => //Current term
        voteTime.time.millis > dateFrom.millis
    }
  }

  private def isDateInTermOfficeRange(
    voteTime: VoteTime,
    termOfOffice: TermOfOffice
  ): Boolean = {
    isDateInRange(
      voteTime,
      sharedDOToSharedDT(termOfOffice.dateFrom.dateFrom),
      termOfOffice.dateTo
        .map(_.dateTo)
        .map(sharedDOToSharedDT)
    )
  }

  def byPersonIdAndRange(
    personId: ParliamentMemberId,
    termOfOffice: TermOfOffice,
    from: SharedDateTime,
    to: SharedDateTime
  ): List[VoteReduced] = {
    val q = quote {
      for {
        p <- query[Vote]
          .filter(
            _.personId.person_id == lift(personId.person_id)
          )
      } yield {
        p
      }
    }

    ctx
      .run(q)
      .filter(
        v => isDateInRange(v.time, from, Some(to))
      )
      .map(toVoteReduced)
  }

  def byPersonIdAndTerm(
    personId: ParliamentMemberId,
    termOfOffice: TermOfOffice
  ): List[VoteReduced] = {
    /* I just want to say that quill sucks big time. Don't waste your time. */
    val q = quote {
      for {
        p <- query[Vote]
          .filter(
            _.personId.person_id == lift(personId.person_id)
          )
      } yield {
        p
      }
    }

    ctx
      .run(q)
      .filter(
        v => isDateInTermOfficeRange(v.time, termOfOffice)
      )
      .map(toVoteReduced)
  }

  private def toVoteReduced(v: Vote): VoteReduced = {
    VoteReduced(v.id, v.vote, v.personId, v.time, None)
  }

  def aggregateDistinctFactions(
    termOfOfficeId: TermOfOfficeId
  ) = {

//    val d = new DateTime(2016, 11, 14).getMillis
//    val q = quote {
//      for {
//        p <- query[Vote]
//          .filter(
//            _.faction.nonEmpty
//          )
//          .filter(_.time.time.millis >= lift(d))
//          .groupBy(_.personId)
//          .map { case (person, votesList) =>
//
//            val personVotesWithFactions = votesList
//              .groupBy(v => v.faction)
//
//            val allFactions = personVotesWithFactions.map(_._1).filter(_.nonEmpty).asInstanceOf[ctx.Query[FactionAcronym]]
//
//            var list = ""
//
//
//
//          }
//      } yield {
//        p
//      }
//    }
//
//    ctx.run(q).filter(_.)
//    println(result)

  }

}
