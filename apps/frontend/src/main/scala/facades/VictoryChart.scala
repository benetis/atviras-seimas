package facades
import japgolly.scalajs.react.CtorType.Props
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import japgolly.scalajs.react._

object VictoryChart {
  @JSImport("victory", "VictoryChart")
  @js.native
  object RawComponent extends js.Object

  @js.native
  trait Props extends js.Object {
    var domain: js.Object = js.native
  }

  def props(domain: js.Object): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.domain = domain
    p
  }

  def component = JsComponent[Props, Children.None, Null](RawComponent)
}
