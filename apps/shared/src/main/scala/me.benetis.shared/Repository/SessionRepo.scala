package me.benetis.shared.Repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.Session
import org.joda.time.DateTime

object SessionRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  implicit val decodeDateTime =
    MappedEncoding[String, DateTime](new DateTime(_))
  implicit val encodeDateTime =
    MappedEncoding[DateTime, String](_.toString("yyyy-MM-dd"))

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
