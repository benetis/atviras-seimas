package me.benetis.coordinator.api
import boopickle.Default._
import java.nio.ByteBuffer
import me.benetis.shared.api.ApiForFrontend
import scala.concurrent.ExecutionContext.Implicits.global

object AutowireServer extends autowire.Server[ByteBuffer, Pickler, Pickler] {

  override def read[Result: Pickler](p: ByteBuffer) =
    Unpickle[Result].fromBytes(p)
  override def write[Result: Pickler](r: Result) = Pickle.intoBytes(r)

  val routes = AutowireServer.route[ApiForFrontend](ApiForFrontendController)
}
