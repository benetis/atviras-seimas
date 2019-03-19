package me.benetis.coordinator

import cats.effect.Sync
import io.circe.Json
import me.benetis.coordinator.downloader.Coordinator
import me.benetis.shared._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class Routes[F[_]: Sync] extends Http4sDsl[F] {
  val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {

      case GET -> Root / "download" / "term-of-office" =>
        downloader.Coordinator(FetchTermOfOffice)
        responseOk()

      case GET -> Root / "download" / "sessions" =>
        downloader.Coordinator(FetchSessions)
        responseOk()

      case GET -> Root / "download" / "plenaries" =>
        downloader.Coordinator(FetchPlenaries)
        responseOk()

      case GET -> Root / "download" / "agenda-questions" =>
        downloader.Coordinator(FetchAgendaQuestions)
        responseOk()
      case GET -> Root / "download" / "plenary-questions" =>
        downloader.Coordinator(FetchPlenaryQuestions)
        responseOk()

      case GET -> Root / "download" / "discussion-events" =>
        downloader.Coordinator(FetchDiscussionEvents)
        responseOk()

      case GET -> Root / "download" / "votes" =>
        downloader.Coordinator(FetchVotes)
        responseOk()
    }

  def responseOk() =
    Ok(Json.obj("message" -> Json.fromString(s"Stuff downloaded")))
}
