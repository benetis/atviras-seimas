package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.coordinator.utils.SqlEncoders
import me.benetis.shared._
import org.joda.time.DateTime

object AgendaQuestionRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  private implicit val encodeDateTime =
    MappedEncoding[DateTimeOnlyTime, String](_.time.toString("HH:mm:ss"))

  implicit val AgendaQuestionStatus =
    MappedEncoding[AgendaQuestionStatus, Int](
      SqlEncoders.AgendaQuestionStatusSerializer)

  implicit val AgendaQuestionSpeakers =
    MappedEncoding[AgendaQuestionSpeakers, String](
      SqlEncoders.AgendaQuestionSpeakersSerializer
    )

  private implicit val SessionInsertMeta = insertMeta[AgendaQuestion]()

  def insert(sessions: Seq[AgendaQuestion]): Unit = {
    val q = quote {
      liftQuery(sessions).foreach(
        e =>
          query[AgendaQuestion]
            .insert(e)
            .onConflictUpdate((t, e) => t.timeFrom -> e.timeFrom,
                              (t, e) => t.timeTo   -> e.timeTo))
    }
    ctx.run(q)
  }

}
