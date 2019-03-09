package me.benetis.coordinator

import cats.effect.Sync
import io.circe.Json
import me.benetis.coordinator.downloader.Coordinator
import me.benetis.shared.{FetchAgendaQuestions, FetchDiscussionEvents, FetchPlenaryQuestions}
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class Routes[F[_]: Sync] extends Http4sDsl[F] {
  val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "download-my-stuff" / "agenda-questions" =>
        downloader.Coordinator(FetchAgendaQuestions)
        responseOk()
      case GET -> Root / "download-my-stuff" / "plenary-questions" =>
        downloader.Coordinator(FetchPlenaryQuestions)
        responseOk()

      case GET -> Root / "download-my-stuff" / "discussion-events" =>
        downloader.Coordinator(FetchDiscussionEvents)
        responseOk()

      case GET -> Root / "api" / "test" => Ok()
    }

  def responseOk() =
    Ok(Json.obj("message" -> Json.fromString(s"Stuff downloaded")))
}
