package me.benetis.downloader.Saver
import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import scala.concurrent.ExecutionContext

object DataSaver {

  val config = new HikariConfig()
  Class.forName("com.mysql.cj.jdbc.Driver")
  config.setJdbcUrl("jdbc:mysql://localhost/")
  config.setUsername("root")
  config.setPassword("not-so-sql")
  config.setMaximumPoolSize(10)

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val transactor: HikariTransactor[IO] = HikariTransactor.apply[IO](
    new HikariDataSource(config),
    ExecutionContext.global,
    ExecutionContext.global)

  val program2 = sql"select 42".query[Int].unique

  val io2 = program2.transact(transactor)

  io2.unsafeRunSync()
}
