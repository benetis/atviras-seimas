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

object Server {
  def main(args: Array[String]) {

    implicit val system       = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route =
      path("hello") {
        get {
          complete(
            HttpEntity(ContentTypes.`text/html(UTF-8)`,
                       "<h1>Say hello to akka-http</h1>"))
        }
      } ~
        path("api" / Segments) { segments =>
          post(AutowireServer.dispatch(segments))
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
//
//    case GET -> Root / "download" / "parliament-members" =>
//    downloader.Coordinator(FetchParliamentMembers)
//    responseCompleted()
//
//    case GET -> Root / "compute" / "mds" =>
//    computing.Coordinator(ComputeMDS)
//    responseCompleted()

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
