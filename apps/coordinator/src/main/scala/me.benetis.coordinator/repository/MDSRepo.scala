package me.benetis.coordinator.repository
import com.typesafe.scalalogging.LazyLogging
import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared._
import org.json4s.native.Serialization.{write, read}
import org.json4s.DefaultFormats

object MDSRepo extends LazyLogging {
  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

  implicit val formats = DefaultFormats

  implicit val eigenEncoding =
    MappedEncoding[EigenValues, String](write(_))

  implicit val mdsProportionEncoding =
    MappedEncoding[MDSProportion, String](write(_))

  implicit val mdsCoordinatesEncoding =
    MappedEncoding[MDSCoordinates, String](write(_))

  implicit val eigenDecoding =
    MappedEncoding[String, EigenValues](read(_))

  implicit val mdsProportionDecoding =
    MappedEncoding[String, MDSProportion](read(_))

  implicit val mdsCoordinatesDecoding =
    MappedEncoding[String, MDSCoordinates](read(_))

  private implicit val MDSInsertMeta = insertMeta[MdsResult]()

  def insert(mds: MdsResult): Unit = {
    val q = quote { query[MdsResult].insert(lift(mds)).onConflictIgnore }

    ctx.run(q)
  }

  def byTermOfOffice(termOfOfficeId: TermOfOfficeId): Option[MdsResult] = {
    val q = quote {
      for {
        p <- query[MdsResult]
          .filter(
            _.termOfOfficeId.term_of_office_id == lift(
              termOfOfficeId.term_of_office_id)
          )
          .sortBy(_.createdAt)
      } yield {
        p
      }
    }

    ctx.run(q).headOption
  }

}
