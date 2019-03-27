package me.benetis.coordinator

import autowire.Core.Request
import cats.effect.{IO, Sync}
import io.circe.Json
import me.benetis.coordinator.api.{ApiForFrontendController, AutowireServer}
import me.benetis.coordinator.computing.ComputeMDS
import me.benetis.coordinator.downloader.Coordinator
import me.benetis.shared._
import me.benetis.shared.api.ApiForFrontend
import org.http4s.{HttpRoutes, Response}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import boopickle.Default._
import java.nio.ByteBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import org.http4s.booPickle._
class Routes[F[_]: Sync] extends Http4sDsl[F] {

  import AutowireServer._

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

      case GET -> Root / "download" / "parliament-members" =>
        downloader.Coordinator(FetchParliamentMembers)
        responseCompleted()

      case GET -> Root / "compute" / "mds" =>
        computing.Coordinator(ComputeMDS)
        responseCompleted()

      case req @ POST -> "api" /: path =>
        implicit val encoder = booEncoderOf[IO, java.nio.ByteBuffer]
        implicit val decoder = booOf[IO, java.nio.ByteBuffer]

        req.decode[String] { data: String =>
          Ok(
            AutowireServer
              .route[ApiForFrontend](ApiForFrontendController)(
                Request(path.toList,
                        Unpickle[Map[String, ByteBuffer]]
                          .fromBytes(ByteBuffer.wrap(data.getBytes())))
              )
          )
        }
    }

  def responseCompleted(): F[Response[F]] =
    Ok(Json.obj("message" -> Json.fromString(s"Completed")))

  def stringResponse(response: String): F[Response[F]] =
    Ok(response)

}
