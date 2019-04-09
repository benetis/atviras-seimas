package me.benetis.coordinator.repository

import com.typesafe.scalalogging.LazyLogging
import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared._
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.{read, write}

object ClusteringRepo extends LazyLogging {
  private lazy val ctx =
    new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._
  import me.benetis.coordinator.utils.SQLVoteEncodersDecoders._

  implicit val formats = DefaultFormats

  implicit val centroidsEncoding =
    MappedEncoding[KMeansCentroids, String](write(_))

  implicit val kMeansPointEncoding =
    MappedEncoding[Vector[KMeansPoint], String](write(_))

  implicit val centroidsDecoding =
    MappedEncoding[String, KMeansCentroids](
      read[KMeansCentroids](_)
    )

  implicit val kMeansDecoding =
    MappedEncoding[String, Vector[KMeansPoint]](
      read[Vector[KMeansPoint]](_)
    )

  private implicit val ClusterMeta =
    insertMeta[KMeansResult]()

  def insert(value: KMeansResult): Unit = {
    val q = quote {
      query[KMeansResult]
        .insert(lift(value))
        .onConflictIgnore
    }

    ctx.run(q)
  }
//
//  def byTermOfOffice(
//    termOfOfficeId: TermOfOfficeId
//  ): Option[MdsResult[MdsPointOnlyXAndY]] = {
//    val q = quote {
//      for {
//        p <- query[MdsResult[MdsPointOnlyXAndY]]
//          .filter(
//            _.termOfOfficeId.term_of_office_id == lift(
//              termOfOfficeId.term_of_office_id
//            )
//          )
//          .sortBy(_.createdAt)(Ord.desc)
//      } yield {
//        p
//      }
//    }
//
//    ctx.run(q).headOption
//  }

}
