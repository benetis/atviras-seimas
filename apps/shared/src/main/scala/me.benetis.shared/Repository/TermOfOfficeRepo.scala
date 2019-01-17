package me.benetis.shared.Repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.{TermOfOffice, TermOfOfficeName}
import org.joda.time.DateTime
object TermOfOfficeRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  private implicit val decodeDateTime =
    MappedEncoding[String, DateTime](new DateTime(_))
  private implicit val encodeDateTime =
    MappedEncoding[DateTime, String](_.toString("yyyy-MM-dd"))

  private implicit val personInsertMeta = insertMeta[TermOfOffice]()

  def insert(terms: Seq[TermOfOffice]) = {
    println(terms)
    val q = quote {
      liftQuery(terms).foreach(
        e =>
          query[TermOfOffice]
            .insert(e)
            .onConflictUpdate((t, e) => t.dateTo -> e.dateTo))
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
