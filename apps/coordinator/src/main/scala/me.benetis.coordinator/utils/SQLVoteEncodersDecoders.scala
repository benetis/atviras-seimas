package me.benetis.coordinator.utils

import io.getquill.{MysqlJdbcContext, SnakeCase}
import me.benetis.shared.VoteEncoding
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.{read, write}

object SQLVoteEncodersDecoders {

  private lazy val ctx =
    new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  implicit val formats = DefaultFormats

  implicit val encoding =
    MappedEncoding[VoteEncoding, String](write(_))

//  implicit val mdsProportionEncoding =
//    MappedEncoding[MDSProportion, String](write(_))
//
//  implicit val mdsCoordinatesEncoding =
//    MappedEncoding[MDSCoordinates[MdsPointOnlyXAndY], String](
//      write(_)
//    )

  implicit val decoding =
    MappedEncoding[String, VoteEncoding](
      read[VoteEncoding](_)
    )

}
