package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.coordinator.repository.DiscussionEventRepo.ctx
import me.benetis.coordinator.utils.{ComputingError, DBNotExpectedResult}
import me.benetis.shared.encoding.EncodersDecoders
import me.benetis.shared._
import org.joda.time.DateTime

object VoteRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

  private implicit val encodeSingleVote =
    MappedEncoding[SingleVote, Int](EncodersDecoders.voteSerializer)

  private implicit val decodeSingleVote =
    MappedEncoding[Int, SingleVote](EncodersDecoders.voteDeserializer)

  private implicit val FactionInsertMeta = insertMeta[Vote]()

  def insert(votes: Seq[Vote]): Unit = {
    val q = quote {
      liftQuery(votes).foreach(
        e =>
          query[Vote]
            .insert(e)
            .onConflictIgnore(_.personId))
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

    ctx.run(q).map(v => VoteReduced(v.id, v.vote, v.personId, v.time, None))
  }

  def listForTermOfOffice(termOfOfficeId: TermOfOfficeId)
    : Either[ComputingError, List[VoteReduced]] = {

    def isDateInRange(voteTime: VoteTime,
                      termOfOffice: TermOfOffice): Boolean = {
      termOfOffice.dateTo match {
        case Some(dateTo) =>
          voteTime.time.millis < dateTo.dateTo.millis && voteTime.time.millis > termOfOffice.dateFrom.dateFrom.millis
        case None => //Current term
          voteTime.time.millis > termOfOffice.dateFrom.dateFrom.millis
      }
    }

    val term = TermOfOfficeRepo.byId(termOfOfficeId)

    term match {
      case Some(termValue) =>
        Right(list().filter(vote => isDateInRange(vote.dateTime, termValue)))
      case None =>
        Left(DBNotExpectedResult(
          s"TermOfOffice with this ID should have been found in the DB. Id: ${termOfOfficeId.term_of_office_id}"))
    }
  }

}
