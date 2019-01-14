package me.benetis.downloader.Repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.{TermOfOffice, TermOfOfficeName}

object TermOfOfficeRepo {

  lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  def printList() = {
    val q = quote {
      for {
        p <- query[TermOfOffice]
      } yield {
        p.name
      }
    }

    val res: List[TermOfOfficeName] = ctx.run(q)

    res.foreach(println)
  }

}
