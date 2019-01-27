package me.benetis.coordinator

import cats.effect.Sync
import io.circe.Json
import me.benetis.coordinator.downloader.DownloaderCoordinator
import me.benetis.shared.{FetchAgendaQuestions, FetchPlenaryQuestions}
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class Routes[F[_]: Sync] extends Http4sDsl[F] {
  val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "download-my-stuff" / "agenda-questions" =>
        DownloaderCoordinator(FetchAgendaQuestions)
        responseOk()
      case GET -> Root / "download-my-stuff" / "plenary-questions" =>
        DownloaderCoordinator(FetchPlenaryQuestions)
        responseOk()
    }

  def responseOk() =
    Ok(Json.obj("message" -> Json.fromString(s"Stuff downloaded")))
}
