package facades
import japgolly.scalajs.react.CtorType.Props
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import japgolly.scalajs.react._
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

  def component = JsComponent[Props, Children.Varargs, Null](RawComponent)
}

object VictoryScatter {
  @JSImport("victory", "VictoryScatter")
  @js.native
  object RawComponent extends js.Object

  @js.native
  trait Props[T] extends js.Object {
    var size: Int         = js.native
    var data: js.Array[T] = js.native
  }

  def props[T](size: Int, data: js.Array[T]): Props[T] = {
    val p = (new js.Object).asInstanceOf[Props[T]]
    p.size = size
    p.data = data
    p
  }

  def component[T] = JsComponent[Props[T], Children.None, Null](RawComponent)
}
