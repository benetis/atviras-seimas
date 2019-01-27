package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared._
import me.benetis.shared.encoding.Encoders

object DiscussionEventRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  private implicit val encodeDateTime =
    MappedEncoding[DateTimeOnlyTime, String](_.time.toString("HH:mm:ss"))

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
