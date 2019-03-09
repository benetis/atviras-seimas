package me.benetis.coordinator.repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.encoding.EncodersDecoders
import me.benetis.shared.{Faction, SingleVote, Vote}
import org.joda.time.DateTime

object VoteRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

  private implicit val encodeSingleVote =
    MappedEncoding[SingleVote, Int](EncodersDecoders.voteSerializer)

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
