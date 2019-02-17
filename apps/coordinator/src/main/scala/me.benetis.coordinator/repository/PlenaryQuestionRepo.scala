package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared._

object PlenaryQuestionRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

  import AgendaQuestionRepo.AgendaQuestionStatus

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
