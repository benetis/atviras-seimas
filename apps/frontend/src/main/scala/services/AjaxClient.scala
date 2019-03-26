//package services
//import org.scalajs.dom.ext.Ajax
//import scala.concurrent.Future
//import upickle.default._
//import scala.concurrent.ExecutionContext.Implicits.global
//
//object AjaxClient extends autowire.Client[String, Reader, Writer] {
//  override def doCall(req: Request): Future[String] = {
//    val url  = "/api/" + req.path.mkString("/")
//    val data = write(req.args)
//    Ajax
//      .post(
//        url = url,
//        data = data
//      )
//      .map(_.responseText)
//  }
//
//  def read[Result: Reader](p: String)  = upickle.default.read[Result](p)
//  def write[Result: Writer](r: Result) = upickle.default.write(r)
//}
