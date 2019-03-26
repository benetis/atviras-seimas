package services
import org.scalajs.dom.ext.Ajax
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import boopickle.Default._
import java.nio.ByteBuffer
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

object AjaxClient extends autowire.Client[ByteBuffer, Pickler, Pickler] {
  override def doCall(req: Request): Future[ByteBuffer] = {
    Ajax
      .post(
        url = "/api/" + req.path.mkString("/"),
        data = Pickle.intoBytes(req.args),
        responseType = "arraybuffer",
        headers = Map("Content-Type" -> "application/octet-stream")
      )
      .map(r => TypedArrayBuffer.wrap(r.response.asInstanceOf[ArrayBuffer]))
  }

  override def read[Result: Pickler](p: ByteBuffer) =
    Unpickle[Result].fromBytes(p)
  override def write[Result: Pickler](r: Result) = Pickle.intoBytes(r)
}
