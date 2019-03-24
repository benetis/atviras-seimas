package me.benetis.coordinator.repository
import com.typesafe.scalalogging.LazyLogging
import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.{Faction, MDSResults}
import org.json4s.native.Serialization.write
import org.json4s.DefaultFormats

object MDSRepo extends LazyLogging {
  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  implicit val formats = DefaultFormats

//  private implicit val MDSInsertMeta = insertMeta[MDSResults]()

  def insert(mds: MDSResults): Unit = {

    val coordinatesV = write(mds.coordinates)
    val eigenValuesV = write(mds.eigenValues)
    val proportionV  = write(mds.proportion)

    logger.info(coordinatesV)

    val rawQuery = quote {
      (coordinates: String,
       eigenValues: String,
       proportion: String,
       term_office_id: Int) =>
        infix"""
                INSERT INTO `atviras-seimas`.mds_result
                VALUES ($coordinates, $term_office_id, NOW(), $eigenValues, $proportion, NULL)"""
          .as[Query[(String, String, String, Int, String)]]
    }

    ctx.run(
      rawQuery(lift(coordinatesV),
               lift(eigenValuesV),
               lift(proportionV),
               lift(mds.termOfOfficeId.term_of_office_id)))
  }

//  def list(): Vector[MDSResults] = {
//    val q = quote {
//      for {
//        p <- query[MDSResults]
//      } yield {
//        p
//      }
//    }
//
//    ctx.run(q).toVector
//  }
}
