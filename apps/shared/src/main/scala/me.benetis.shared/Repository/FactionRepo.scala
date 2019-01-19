package me.benetis.shared.Repository

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

//  private def printList() = {
//    val q = quote {
//      for {
//        p <- query[TermOfOffice]
//      } yield {
//        p.name
//      }
//    }
//
//    val res: List[TermOfOfficeName] = ctx.run(q)
//
//    res.foreach(println)
//  }

}
