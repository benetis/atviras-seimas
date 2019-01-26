package me.benetis.coordinator

import cats.effect.Sync
import io.circe.Json
import me.benetis.coordinator.downloader.DownloaderCoordinator
import me.benetis.shared.FetchAgendaQuestions
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class Routes[F[_]: Sync] extends Http4sDsl[F] {
  val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "download-my-stuff" =>
        DownloaderCoordinator(FetchAgendaQuestions)
        Ok(Json.obj("message" -> Json.fromString(s"Stuff downloaded")))
    }
}
