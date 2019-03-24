package me.benetis.coordinator.repository
import com.typesafe.scalalogging.LazyLogging
import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared._
import org.json4s.native.Serialization.write
import org.json4s.DefaultFormats

object MDSRepo extends LazyLogging {
  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  implicit val formats = DefaultFormats

  implicit val eigenEncoding =
    MappedEncoding[EigenValues, String](write(_))

  implicit val mdsProportionEncoding =
    MappedEncoding[MDSProportion, String](write(_))

  implicit val mdsCoordinatesEncoding =
    MappedEncoding[MDSCoordinates, String](write(_))

  private implicit val MDSInsertMeta = insertMeta[MdsResult]()

  def insert(mds: MdsResult): Unit = {
    val q = quote { query[MdsResult].insert(lift(mds)).onConflictIgnore }

    ctx.run(q)
  }

}
