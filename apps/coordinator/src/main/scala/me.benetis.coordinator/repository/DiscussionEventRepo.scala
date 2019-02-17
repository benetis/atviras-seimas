package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared._
import me.benetis.shared.encoding.Encoders

object DiscussionEventRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

  implicit val AgendaQuestionStatus =
    MappedEncoding[DiscussionEventType, Int](Encoders.discussionEventSerializer)

  implicit val voteTypeEnc =
    MappedEncoding[VoteType, Int](Encoders.voteTypeSerialize)

  private implicit val SessionInsertMeta = insertMeta[DiscussionEvent]()

  def insert(sessions: Seq[DiscussionEvent]): Unit = {
    val q = quote {
      liftQuery(sessions).foreach(
        e =>
          query[DiscussionEvent]
            .insert(e)
      )
    }
    ctx.run(q)
  }

}
