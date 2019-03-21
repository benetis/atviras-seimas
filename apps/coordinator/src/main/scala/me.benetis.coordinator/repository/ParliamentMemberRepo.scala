package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.coordinator.utils.SQLDateEncodersDecoders._
import me.benetis.shared.{ParliamentMember, Session}
import me.benetis.shared._
import me.benetis.shared.encoding.EncodersDecoders

object ParliamentMemberRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  private implicit val parliamentMemberIm = insertMeta[ParliamentMember]()

  def insert(sessions: Seq[ParliamentMember]): Unit = {
    val q = quote {
      liftQuery(sessions).foreach(
        e =>
          query[ParliamentMember]
            .insert(e))
    }
    ctx.run(q)
  }

  def list(): List[ParliamentMember] = {
    val q = quote {
      for {
        p <- query[ParliamentMember]
      } yield {
        p
      }
    }

    ctx.run(q)
  }

}
