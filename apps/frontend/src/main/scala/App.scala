import org.scalajs.dom
import scala.scalajs.js.annotation.JSExport
import scalacss.internal.mutable.GlobalRegistry

object App {

  @JSExport
  def main(args: Array[String]): Unit = {

    import globalStyles.CssSettings._

    AppRouter.router.renderIntoDOM(
      dom.document.getElementById("root-container")
    )

    GlobalRegistry.addToDocumentOnRegistration()
  }

}
