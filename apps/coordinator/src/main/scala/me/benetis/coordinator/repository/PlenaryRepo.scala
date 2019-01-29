package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.{Plenary, PlenaryId, Session}
import org.joda.time.DateTime

object PlenaryRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

  private implicit val SessionInsertMeta = insertMeta[Plenary]()

  def insert(plenaries: Seq[Plenary]): Unit = {
    val q = quote {
      liftQuery(plenaries).foreach(
        e =>
          query[Plenary]
            .insert(e)
            .onConflictUpdate((t, e) => { //Update both timeString start/end
              t.timeFinish -> e.timeFinish
            }, (t, e) => t.timeStart -> e.timeStart))
    }
    ctx.run(q)
  }

  def list(): List[Plenary] = {
    val q = quote {
      for {
        p <- query[Plenary]
      } yield {
        p
      }
    }

    ctx.run(q)
  }

  def findById(plenaryId: PlenaryId): Option[Plenary] = {
    val q = quote {
      for {
        p <- query[Plenary].filter(
          _.id.plenary_id == lift(plenaryId.plenary_id))
      } yield {
        p
      }
    }

    ctx.run(q).headOption
  }

}
