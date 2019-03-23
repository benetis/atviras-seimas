package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.{TermOfOffice, TermOfOfficeId}

object TermOfOfficeRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

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

  def list(): List[TermOfOffice] = {
    val q = quote {
      for {
        p <- query[TermOfOffice]
      } yield {
        p
      }
    }

    ctx.run(q)
  }

  def byId(termOfOfficeId: TermOfOfficeId): Option[TermOfOffice] = {
    val q = quote {
      for {
        p <- query[TermOfOffice].filter(
          _.id.term_of_office_id == lift(termOfOfficeId.term_of_office_id))
      } yield {
        p
      }
    }

    ctx.run(q).headOption
  }

}
