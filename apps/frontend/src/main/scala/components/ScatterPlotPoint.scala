package components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.HtmlTagOf
import japgolly.scalajs.react.vdom.PackageBase.VdomAttr
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom
import scalacss.internal.mutable.GlobalRegistry
import utils.HtmlElementDynamic

object ScatterPlotPoint {

  import styles.CssSettings._

  GlobalRegistry.register(new Style)
  val style = GlobalRegistry[Style].get

  case class Props(x: Double, y: Double, datum: Double)

  val component = ScalaComponent
    .builder[Unit]("Scatter plot point")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Unit, Unit]) {
    val text = HtmlTagOf[HtmlElementDynamic]("text")

    def render(p: Unit, s: Unit) = {

      dom.console.log(p)

      text(VdomAttr("x") := 1.0,
           VdomAttr("y") := 1.0,
           "test")

    }
  }

  def apply() = component

  class Style extends StyleSheet.Inline {}
}
