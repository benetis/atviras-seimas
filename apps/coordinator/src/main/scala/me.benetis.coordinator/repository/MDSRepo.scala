package me.benetis.coordinator.repository
import com.typesafe.scalalogging.LazyLogging
import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared._
import org.json4s.native.Serialization.{write, read}
import org.json4s.DefaultFormats

object MDSRepo extends LazyLogging {
  private lazy val ctx =
    new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._

  implicit val formats = DefaultFormats

  implicit val eigenEncoding =
    MappedEncoding[EigenValues, String](write(_))

  implicit val mdsProportionEncoding =
    MappedEncoding[MDSProportion, String](write(_))

  implicit val mdsCoordinatesEncoding =
    MappedEncoding[MDSCoordinates[MdsPointOnlyXAndY],
                   String](write(_))

  implicit val eigenDecoding =
    MappedEncoding[String, EigenValues](
      read[EigenValues](_))

  implicit val mdsProportionDecoding =
    MappedEncoding[String, MDSProportion](
      read[MDSProportion](_))

  implicit val mdsCoordinatesDecoding =
    MappedEncoding[String,
                   MDSCoordinates[MdsPointOnlyXAndY]](
      (coords: String) => {
        read[MDSCoordinates[MdsPointOnlyXAndY]](coords)
      })

  private implicit val MDSInsertMeta =
    insertMeta[MdsResult[MdsPointOnlyXAndY]]()

  def insert(mds: MdsResult[MdsPointOnlyXAndY]): Unit = {
    val q = quote {
      query[MdsResult[MdsPointOnlyXAndY]]
        .insert(lift(mds))
        .onConflictIgnore
    }

    ctx.run(q)
  }

  def byTermOfOffice(termOfOfficeId: TermOfOfficeId)
    : Option[MdsResult[MdsPointOnlyXAndY]] = {
    val q = quote {
      for {
        p <- query[MdsResult[MdsPointOnlyXAndY]]
          .filter(
            _.termOfOfficeId.term_of_office_id == lift(
              termOfOfficeId.term_of_office_id)
          )
          .sortBy(_.createdAt)(Ord.desc)
      } yield {
        p
      }
    }

    ctx.run(q).headOption
  }

}
