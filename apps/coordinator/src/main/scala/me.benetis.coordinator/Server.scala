package me.benetis.coordinator
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import me.benetis.coordinator.api.AutowireServer
import me.benetis.shared.api.ApiForFrontend
import scala.io.StdIn
import AutowireServer._
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.model.StatusCodes._
import me.benetis.coordinator.computing.{
  ComputeKMeans,
  ComputeMDS,
  ComputeMultiFactionsItems
}
import me.benetis.shared.{
  FetchFactions,
  FetchParliamentMembers
}

object Server {
  def main(args: Array[String]) {

    val downloaded = complete(
      HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        "<h1>Downloaded</h1>"
      )
    )

    implicit val system       = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route =
      path("compute" / "mds") {
        get {
          computing.Coordinator(ComputeMDS)
          complete(
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              "<h1>Computed MDS</h1>"
            )
          )
        }
      } ~ path("compute" / "k-means") {
        get {
          computing.Coordinator(ComputeKMeans)
          complete(
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              "<h1>Computed KMeans</h1>"
            )
          )
        }
      } ~ path("compute" / "multi-factions-list") {
        get {
          computing.Coordinator(ComputeMultiFactionsItems)
          complete(
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              "<h1>Computed multi faction list</h1>"
            )
          )
        }
      } ~ path("download" / "parliament-members") {
        get {
          downloader.Coordinator(FetchParliamentMembers)
          downloaded
        }
      } ~ path("download" / "faction") {
        get {
          downloader.Coordinator(FetchFactions)
          downloaded
        }
      } ~
        path("api" / Segments) { segments =>
          post(AutowireServer.dispatch(segments))
        }

    implicit def myExceptionHandler: ExceptionHandler =
      ExceptionHandler {
        case e: Throwable =>
          extractUri { uri =>
            println(
              s"Request to $uri could not be handled normally"
            )
            println(e.getMessage)
            println(e.printStackTrace())

            complete(
              HttpResponse(
                InternalServerError,
                entity = "Some random exception"
              )
            )
          }
      }

//    case GET -> Root / "download" / "term-of-office" =>
//    downloader.Coordinator(FetchTermOfOffice)
//    responseCompleted()
//
//    case GET -> Root / "download" / "sessions" =>
//    downloader.Coordinator(FetchSessions)
//    responseCompleted()
//
//    case GET -> Root / "download" / "plenaries" =>
//    downloader.Coordinator(FetchPlenaries)
//    responseCompleted()
//
//    case GET -> Root / "download" / "agenda-questions" =>
//    downloader.Coordinator(FetchAgendaQuestions)
//    responseCompleted()
//    case GET -> Root / "download" / "plenary-questions" =>
//    downloader.Coordinator(FetchPlenaryQuestions)
//    responseCompleted()
//
//    case GET -> Root / "download" / "discussion-events" =>
//    downloader.Coordinator(FetchDiscussionEvents)
//    responseCompleted()
//
//    case GET -> Root / "download" / "votes" =>
//    downloader.Coordinator(FetchVotes)
//    responseCompleted()

    val bindingFuture =
      Http().bindAndHandle(route, "0.0.0.0", 8080)

    println(
      s"Server online at http://0.0.0.0:8080/\nPress RETURN to stop..."
    )
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind())                 // trigger unbinding rangeFrom the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
