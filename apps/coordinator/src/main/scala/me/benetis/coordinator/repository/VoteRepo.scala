package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.encoding.Encoders
import me.benetis.shared.{Faction, SingleVote, Vote}
import org.joda.time.DateTime

object VoteRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  private implicit val encodeDateTime =
    MappedEncoding[DateTime, String](_.toString("yyyy-MM-dd HH:mm:ss"))

  private implicit val encodeSingleVote =
    MappedEncoding[SingleVote, Int](Encoders.voteSerializer)

  private implicit val FactionInsertMeta = insertMeta[Vote]()

  def insert(votes: Seq[Vote]): Unit = {
    val q = quote {
      liftQuery(votes).foreach(
        e =>
          query[Vote]
            .insert(e)
            .onConflictIgnore(_.personId))
    }
    ctx.run(q)
  }

}
