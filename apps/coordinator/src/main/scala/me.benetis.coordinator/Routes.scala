package me.benetis.coordinator

import cats.effect.Sync
import io.circe.Json
import me.benetis.coordinator.computing.ComputeMDS
import me.benetis.coordinator.downloader.Coordinator
import me.benetis.shared._
import org.http4s.{HttpRoutes, Response}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class Routes[F[_]: Sync] extends Http4sDsl[F] {
  val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "download" / "term-of-office" =>
        downloader.Coordinator(FetchTermOfOffice)
        responseCompleted()

      case GET -> Root / "download" / "sessions" =>
        downloader.Coordinator(FetchSessions)
        responseCompleted()

      case GET -> Root / "download" / "plenaries" =>
        downloader.Coordinator(FetchPlenaries)
        responseCompleted()

      case GET -> Root / "download" / "agenda-questions" =>
        downloader.Coordinator(FetchAgendaQuestions)
        responseCompleted()
      case GET -> Root / "download" / "plenary-questions" =>
        downloader.Coordinator(FetchPlenaryQuestions)
        responseCompleted()

      case GET -> Root / "download" / "discussion-events" =>
        downloader.Coordinator(FetchDiscussionEvents)
        responseCompleted()

      case GET -> Root / "download" / "votes" =>
        downloader.Coordinator(FetchVotes)
        responseCompleted()

      case GET -> Root / "compute" / "mds" =>
        computing.Coordinator(ComputeMDS)
        responseCompleted()
    }

  def responseCompleted(): F[Response[F]] =
    Ok(Json.obj("message" -> Json.fromString(s"Completed")))

  def stringResponse(response: String): F[Response[F]] =
    Ok(response)

}
