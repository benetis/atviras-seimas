package me.benetis.coordinator.repository

import com.typesafe.scalalogging.LazyLogging
import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.coordinator.utils.SQLDateEncodersDecoders._
import me.benetis.shared.{ParliamentMember, Session}
import me.benetis.shared._
import me.benetis.shared.encoding.EncodersDecoders

object ParliamentMemberRepo extends LazyLogging {

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

  def listByTermOfOffice(
      termOfOfficeId: TermOfOfficeId): List[ParliamentMember] = {
    val q = quote {
      for {
        p <- query[ParliamentMember].filter(
          _.termOfOfficeId.term_of_office_id == lift(
            termOfOfficeId.term_of_office_id))
      } yield {
        p
      }
    }

    ctx.run(q)
  }

  def updateTermsSpecificIds(): Unit = {

    logger.info("Start specific ids")

    val terms = TermOfOfficeRepo.list().map(_.id)

    terms.foreach((t: TermOfOfficeId) => {
      val members: List[ParliamentMember] = listByTermOfOffice(t)
      val updated = members.zipWithIndex.map {
        case (m, i) =>
          m.copy(
            termOfOfficeSpecificId =
              Some(ParliamentMemberTermOfOfficeSpecificId(i)))
      }

      updateSpecificIds(updated)

    })

  }

  private def updateSpecificIds(members: List[ParliamentMember]): Unit = {

    //Unsafe as it requires specific id to be set, but works

    val q = quote {
      liftQuery(members).foreach { person =>
        query[ParliamentMember]
          .filter(p => p.personId.person_id == person.personId.person_id)
          .update(p =>
            p.termOfOfficeSpecificId
              .map(_.term_of_office_specific_id) -> person.termOfOfficeSpecificId
              .map(_.term_of_office_specific_id))
      }
    }

    ctx.run(q)

  }

}
