package me.benetis.coordinator.api
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{
  RequestContext,
  RouteResult
}
import akka.util.{ByteString, CompactByteString}
import boopickle.Default._
import com.typesafe.scalalogging.LazyLogging
import java.nio.ByteBuffer
import me.benetis.shared.api.ApiForFrontend
import scala.concurrent.{ExecutionContext, Future}
import java.nio.charset.{Charset, StandardCharsets}

object AutowireServer
    extends autowire.Server[ByteBuffer, Pickler, Pickler]
    with LazyLogging {
  override def read[R: Pickler](p: ByteBuffer) =
    Unpickle[R].fromBytes(p)
  override def write[R: Pickler](r: R) = Pickle.intoBytes(r)

  def dispatch(url: List[String])(
      implicit ec: ExecutionContext)
    : RequestContext => Future[RouteResult] =
    entity(as[ByteString]) { entity =>
      val body =
        Unpickle[Map[String, ByteBuffer]]
          .fromBytes(entity.asByteBuffer)
      val request: Future[ByteBuffer] =
        AutowireServer.route[ApiForFrontend](
          ApiForFrontendController)(
          autowire.Core.Request(url, body))
      onSuccess(request)((buffer: ByteBuffer) => {
        complete(CompactByteString(buffer))
      })
    }
}
