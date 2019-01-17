package me.benetis.shared.Repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.{Plenary, Session}
import org.joda.time.DateTime

object PlenaryRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  private implicit val encodeDateTime =
    MappedEncoding[DateTime, String](_.toString("yyyy-MM-dd HH:mm:ss"))
  private implicit val SessionInsertMeta = insertMeta[Plenary]()

  def insert(plenaries: Seq[Plenary]): Unit = {
    val q = quote {
      liftQuery(plenaries).foreach(
        e =>
          query[Plenary]
            .insert(e)
            .onConflictUpdate((t, e) => { //Update both time start/end
              t.timeFinish -> e.timeFinish
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
