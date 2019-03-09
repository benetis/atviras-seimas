package me.benetis.coordinator.repository

import com.typesafe.scalalogging.LazyLogging
import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared._
import me.benetis.shared.encoding.EncodersDecoders
import org.joda.time.DateTime

object AgendaQuestionEncodings extends LazyLogging {
  val agendaQuestionStatusEncoderMap: Map[AgendaQuestionStatus, Int] = Map(
    Adoption                          -> 0,
    Discussion                        -> 1,
    Affirmation                       -> 2,
    Presentation                      -> 3,
    PresentationOfReturnedLawDocument -> 4,
    Question                          -> 5,
    InterpolationAnalysis             -> 6,
    UnknownStatus                     -> 7
  )

  def agendaQuestionStatusSerializer(
                                      AgendaQuestionStatus: AgendaQuestionStatus): Int = {
    agendaQuestionStatusEncoderMap.getOrElse(AgendaQuestionStatus, -1)
  }

  def agendaQuestionStatusDeserializer(status: Int): AgendaQuestionStatus = {
    agendaQuestionStatusEncoderMap.find(_._2 == status) match {
      case Some(stat) => stat._1
      case None =>
        logger.error(s"Not supported status <decode db> '$status'")
        UnknownStatus
    }
  }
}

object AgendaQuestionRepo {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

  implicit val AgendaQuestionStatus =
    MappedEncoding[AgendaQuestionStatus, Int](
      AgendaQuestionEncodings.agendaQuestionStatusSerializer)

  implicit val AgendaQuestionStatusDecoder =
    MappedEncoding[Int, AgendaQuestionStatus](
      AgendaQuestionEncodings.agendaQuestionStatusDeserializer)

  implicit val AgendaQuestionSpeakers =
    MappedEncoding[AgendaQuestionSpeakers, String](
      EncodersDecoders.AgendaQuestionSpeakersSerializer
    )

  implicit val AgendaQuestionSpeakersDes =
    MappedEncoding[String, AgendaQuestionSpeakers](
      EncodersDecoders.AgendaQuestionSpeakersDeSerializer
    )



  private implicit val SessionInsertMeta = insertMeta[AgendaQuestion]()

  def insert(sessions: Seq[AgendaQuestion]): Unit = {
    val q = quote {
      liftQuery(sessions).foreach(
        e =>
          query[AgendaQuestion]
            .insert(e)
            .onConflictUpdate((t, e) => t.timeFrom -> e.timeFrom,
                              (t, e) => t.timeTo   -> e.timeTo))
    }
    ctx.run(q)
  }

  def list(): List[AgendaQuestion] = {
    val q = quote {
      for {
        p <- query[AgendaQuestion]
      } yield {
        p
      }
    }

    ctx.run(q)
  }

  def listBeforePlenary(plenaryId: PlenaryId): List[AgendaQuestion] = {
    val q = quote {
      for {
        p <- query[AgendaQuestion].filter(_.plenaryId.plenary_id > lift(plenaryId.plenary_id))
      } yield {
        p
      }
    }

    ctx.run(q)
  }

}
