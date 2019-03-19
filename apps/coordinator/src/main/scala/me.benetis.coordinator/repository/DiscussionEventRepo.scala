package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared._
import me.benetis.shared.encoding.EncodersDecoders

object DiscussionEventRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

  implicit val AgendaQuestionStatus =
    MappedEncoding[DiscussionEventType, Int](
      EncodersDecoders.discussionEventSerializer)

  implicit val voteTypeEnc =
    MappedEncoding[VoteType, Int](EncodersDecoders.voteTypeSerialize)

  private implicit val SessionInsertMeta = insertMeta[DiscussionEvent]()

  def insert(events: Seq[DiscussionEvent]): Unit = {
    val q = quote {
      liftQuery(events).foreach(
        e =>
          query[DiscussionEvent]
            .insert(e)
            .onConflictIgnore(_.uniqueId)
      )
    }
    ctx.run(q)
  }

  def distinctVoteIds(): List[VoteId] = {
    val q = quote {
      for {
        p <- query[DiscussionEvent].map(_.voteId).filter(_.nonEmpty).distinct
      } yield {
        p
      }
    }

    val res: List[Option[VoteId]] = ctx.run(q)

    res.flatten
  }

}
