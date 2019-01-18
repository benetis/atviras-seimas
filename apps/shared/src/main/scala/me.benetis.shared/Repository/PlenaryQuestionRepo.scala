package me.benetis.shared.Repository

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared._
import org.joda.time.DateTime

object PlenaryQuestionRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  implicit val decodeDateTime =
    MappedEncoding[String, DateTime](new DateTime(_))
  private implicit val encodeDateTime =
    MappedEncoding[DateTime, String](_.toString("HH:mm:ss"))

  implicit val PlenaryQuestionStatus =
    MappedEncoding[PlenaryQuestionStatus, Int](
      Encoders.plenaryQuestionStatusSerializer)

  implicit val PlenaryQuestionSpeakers =
    MappedEncoding[PlenaryQuestionSpeakers, String](
      Encoders.plenaryQuestionSpeakersSerializer
    )

  private implicit val SessionInsertMeta = insertMeta[PlenaryQuestion]()

  def insert(sessions: Seq[PlenaryQuestion]): Unit = {
    val q = quote {
      liftQuery(sessions).foreach(
        e =>
          query[PlenaryQuestion]
            .insert(e)
            .onConflictUpdate((t, e) => t.timeFrom -> e.timeFrom,
                              (t, e) => t.timeTo -> e.timeTo))
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
