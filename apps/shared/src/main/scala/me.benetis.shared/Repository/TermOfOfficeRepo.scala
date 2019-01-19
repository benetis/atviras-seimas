package me.benetis.shared.Repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.{DateTimeOnlyDate, TermOfOffice}

object TermOfOfficeRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  implicit val encodeDateTime =
    MappedEncoding[DateTimeOnlyDate, String](_.date.toString("yyyy-MM-dd"))

  private implicit val personInsertMeta = insertMeta[TermOfOffice]()

  def insert(terms: Seq[TermOfOffice]) = {
    val q = quote {
      liftQuery(terms).foreach(
        e =>
          query[TermOfOffice]
            .insert(e)
            .onConflictUpdate((t, e) => t.dateTo -> e.dateTo))
    }
    ctx.run(q)
  }

}
