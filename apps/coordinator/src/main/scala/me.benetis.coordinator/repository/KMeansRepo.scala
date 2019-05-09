package me.benetis.coordinator.repository

import com.typesafe.scalalogging.LazyLogging
import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.coordinator.utils.SQLVoteEncodersDecoders
import me.benetis.shared._
import me.benetis.shared.encoding.VoteEncoding
import me.benetis.shared.encoding.VoteEncoding.VoteEncodingE1
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.{read, write}

object KMeansRepo extends LazyLogging {
  private lazy val ctx =
    new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  import me.benetis.coordinator.utils.SQLDateEncodersDecoders._
  import me.benetis.coordinator.utils.SQLVoteEncodersDecoders._

  implicit val formats = DefaultFormats

  implicit val centroidsEncoding =
    MappedEncoding[KMeansCentroids, String](write(_))

  implicit val centroidsDecoding =
    MappedEncoding[String, KMeansCentroids](
      read[KMeansCentroids](_)
    )

  implicit val mdsCoordinatesEncoding =
    MappedEncoding[MDSCoordinates[KMeansPoint], String](
      write(_)
    )

  implicit val mdsCoordinatesDecoding =
    MappedEncoding[String, MDSCoordinates[
      KMeansPoint
    ]]((coords: String) => {

      read[MDSCoordinates[KMeansPoint]](coords)
    })

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

  def byTermOfOffice(
    termOfOfficeId: TermOfOfficeId
  ): List[KMeansResult] = {
    val q = quote {
      for {
        p <- query[KMeansResult]
          .filter(
            _.termOfOfficeId.term_of_office_id == lift(
              termOfOfficeId.term_of_office_id
            )
          )
          .sortBy(_.totalClusters.total_clusters)(Ord.asc)
      } yield {
        p
      }
    }

    ctx.run(q)
  }

}
