package facades
import components.ScatterPlotPoint
import japgolly.scalajs.react.CtorType.Props
import scala.scalajs.js
import japgolly.scalajs.react.raw.React.Element
import scala.scalajs.js.annotation.{
  JSImport,
  ScalaJSDefined
}
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.React.ComponentElement
import japgolly.scalajs.react.vdom.VdomNode

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

  def component =
    JsComponent[Props, Children.Varargs, Null](RawComponent)
}

@js.native
trait VictoryStyleObject extends js.Object {
  val fill: js.Function1[Any, String] = js.native
}

@js.native
trait VictoryStyleInterface extends js.Object {
//  var parent: VictoryStyleObject = js.native
  val data: js.UndefOr[VictoryStyleObject] = js.undefined
//  var labels: VictoryStyleObject = js.native
}

object VictoryScatter {
  @JSImport("victory", "VictoryScatter")
  @js.native
  object RawComponent extends js.Object

  @js.native
  trait Props extends js.Object {
    var size: Int                  = js.native
    var data: js.Array[js.Dynamic] = js.native
    var style: js.Object           = js.native
    var dataComponent: Element     = js.native
  }

  def props(
      size: Int,
      data: js.Array[js.Dynamic],
      style: js.Object,
      dataComponent: Option[Element] = None): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.size = size
    p.data = data
    p.style = style
    dataComponent.foreach(p.dataComponent = _)
    p
  }

  def component =
    JsComponent[Props, Children.None, Null](RawComponent)
}
