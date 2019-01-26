package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.coordinator.utils.SqlEncoders
import me.benetis.shared._

object PlenaryQuestionRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  private implicit val encodeDateTime =
    MappedEncoding[DateTimeOnlyTime, String](_.time.toString("HH:mm:ss"))

  implicit val AgendaQuestionStatus =
    MappedEncoding[PlenaryQuestionStatus, Int](
      SqlEncoders.plenaryQuestionStatusSerializer)

  private implicit val SessionInsertMeta = insertMeta[PlenaryQuestion]()

  def insert(sessions: Seq[PlenaryQuestion]): Unit = {
    val q = quote {
      liftQuery(sessions).foreach(
        e =>
          query[PlenaryQuestion]
            .insert(e)
            .onConflictIgnore(_.plenaryQuestionGroupId)
      )
    }
    ctx.run(q)
  }

}
