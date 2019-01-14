package me.benetis.downloader

import cats.effect.{Effect, ExitCode, IO, IOApp}
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext
import cats.implicits._
import fs2.StreamApp

object HelloWorldServer extends IOApp {
  import scala.concurrent.ExecutionContext.Implicits.global

  def run(args: List[String]): IO[ExitCode] =
    ServerStream.stream[IO].compile.drain.as(ExitCode.Success)
}

object ServerStream {

  def helloWorldService[F[_]: Effect] = new HelloWorldService[F].service

  def stream[F[_]: Effect](implicit ec: ExecutionContext) =
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(helloWorldService, "/")
      .serve
}
