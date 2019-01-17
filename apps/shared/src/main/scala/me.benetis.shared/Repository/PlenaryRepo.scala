package me.benetis.shared.Repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.{Plenary, Session}

object PlenaryRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  private implicit val SessionInsertMeta = insertMeta[Plenary]()

  def insert(plenaries: Seq[Plenary]): Unit = {
    val q = quote {
      liftQuery(plenaries).foreach(
        e =>
          query[Plenary]
            .insert(e)
            .onConflictUpdate((t, e) => { //Update both time start/end
              t.timeEnd -> e.timeEnd
            }, (t, e) => t.timeStart -> e.timeStart))
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
