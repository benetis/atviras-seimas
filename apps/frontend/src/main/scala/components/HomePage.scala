package components

import diode.react.ModelProxy
import facades.{VictoryChart, VictoryScatter}
import japgolly.scalajs.react._
import me.benetis.shared.{
  MdsPoint,
  MdsResult,
  SessionId,
  TermOfOfficeId
}
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry
import services.{LoadMdsResult, RootModel}
import japgolly.scalajs.react.vdom.html_<^._
import scala.scalajs.js
import js.JSConverters._

object HomePage {

  import styles.CssSettings._

  GlobalRegistry.register(new Style)
  val style = GlobalRegistry[Style].get

  case class Props(proxy: ModelProxy[Option[MdsResult]])

  private val component = ScalaComponent
    .builder[Props]("Home page")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props, Unit]) {

    val fill: js.Function1[MdsPoint, String] =
      (point: MdsPoint) =>
        if (point.factionName.faction_name == "Lietuvos socialdemokratų partija")
          "#ff0000"
        else "#00ff00"

    def render(p: Props, s: Unit) = {
      <.div(
        <.div("inside render"),
        <.button(
          "MDS",
          ^.onClick --> {
            p.proxy.dispatchCB(
              LoadMdsResult(TermOfOfficeId(8)))
          }
        ),
        <.p("Mds data:"),
        p.proxy.value.fold(<.div("Empty MDS"))(
          (result: MdsResult) =>
            <.div(
              VictoryChart.component(
                VictoryChart.props(js.Dynamic.literal()))(
                VictoryScatter.component(
                  VictoryScatter.props(
                    size = 3,
                    data = result.coordinates.value
                      .map((p: MdsPoint) => {
                        val x: js.Dynamic =
                          js.Dynamic.literal("x" -> p.x,
                                             "y" -> p.y)
                        x
                      })
                      .toJSArray,
                    js.Dynamic.literal("data" -> js.Dynamic
                      .literal("fill" -> "#ff0000"))
                  )
                )
              ),
              s"Total points: ${result.coordinates.value.length}"
          ),
        )
      )
    }
  }

  def apply(props: Props) = component(props)

  class Style extends StyleSheet.Inline {

    import dsl._

    val test = style(
      backgroundColor.red
    )

  }
}
