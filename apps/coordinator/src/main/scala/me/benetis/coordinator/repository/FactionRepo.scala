package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.Faction

object FactionRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  private implicit val FactionInsertMeta = insertMeta[Faction]()

  def insert(factions: Seq[Faction]): Unit = {
    val q = quote {
      liftQuery(factions).foreach(
        e =>
          query[Faction]
            .insert(e))
    }
    ctx.run(q)
  }

  def list(): Vector[Faction] = {
    val q = quote {
      for {
        p <- query[Faction]
      } yield {
        p
      }
    }

    ctx.run(q).toVector
  }

}
