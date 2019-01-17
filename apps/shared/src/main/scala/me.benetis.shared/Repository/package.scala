package me.benetis.shared

import io.getquill.{MysqlJdbcContext, SnakeCase}
import org.joda.time.DateTime

package object Repository {

  private lazy val ctx = new MysqlJdbcContext(SnakeCase, "ctx")

  import ctx._

  implicit val decodeDateTime =
    MappedEncoding[String, DateTime](new DateTime(_))
  implicit val encodeDateTime =
    MappedEncoding[DateTime, String](_.toString("yyyy-MM-dd"))

}
