package components

import diode.react.ModelProxy
import facades.{VictoryChart, VictoryScatter}
import japgolly.scalajs.react._
import me.benetis.shared.{
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
import me.benetis.shared.Common.Point

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

    //        <VictoryChart
//        theme={VictoryTheme.material}
//        domain={{ x: [0, 5], y: [0, 7] }}
//          >
//          <VictoryScatter
//          style={{ data: { fill: "#c43a31" } }}
//            size={7}
//            data={[
//          { x: 1, y: 2 },
//          { x: 2, y: 3 },
//          { x: 3, y: 5 },

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
                    3,
                    result.coordinates.value
                      .map(p => {
                        val x: js.Dynamic =
                          js.Dynamic.literal("x" -> p.x,
                                             "y" -> p.y)
                        x
                      })
                      .toJSArray
                  )
                )
              )
          )
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
