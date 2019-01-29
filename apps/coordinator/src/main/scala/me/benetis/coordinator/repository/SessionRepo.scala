package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.Session
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object SessionRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

  private implicit val SessionInsertMeta = insertMeta[Session]()

  def insert(sessions: Seq[Session]): Unit = {
    val q = quote {
      liftQuery(sessions).foreach(
        e =>
          query[Session]
            .insert(e)
            .onConflictUpdate((t, e) => t.date_to -> e.date_to))
    }
    ctx.run(q)
  }

  def list(): Vector[Session] = {
    val q = quote {
      for {
        p <- query[Session]
      } yield {
        p
      }
    }

    ctx.run(q).toVector
  }

}
