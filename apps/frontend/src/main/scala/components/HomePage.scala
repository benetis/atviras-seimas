package components

import diode.react.ModelProxy
import facades._
import japgolly.scalajs.react._
import me.benetis.shared.{
  MdsPoint,
  MdsPointWithAdditionalInfo,
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
import org.scalajs.dom

object HomePage {

  import styles.CssSettings._

  GlobalRegistry.register(new Style)
  val style = GlobalRegistry[Style].get

  case class PointColor(value: String)

  case class Props(
      proxy: ModelProxy[
        Option[MdsResult[MdsPointWithAdditionalInfo]]])

  private val component = ScalaComponent
    .builder[Props]("Home page")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props, Unit]) {

    val fill: js.Function1[js.Dynamic, String] =
      (point: js.Dynamic) =>
        factionColor(
          point.faction_name
            .asInstanceOf[String]).value

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
          (result: MdsResult[MdsPointWithAdditionalInfo]) =>
            <.div(
              VictoryChart.component(
                VictoryChart.props(js.Dynamic.literal()))(
                VictoryScatter.component(
                  VictoryScatter.props(
                    size = 3,
                    data = result.coordinates.value
                      .map((p: MdsPointWithAdditionalInfo) => {
                        val x: js.Dynamic =
                          js.Dynamic.literal(
                            "x"            -> p.x,
                            "y"            -> p.y,
                            "faction_name" -> p.factionName.faction_name)
                        x
                      })
                      .toJSArray,
                    js.Dynamic
                      .literal(
                        "data" -> js.Dynamic
                          .literal("fill" -> fill)
                          .asInstanceOf[VictoryStyleObject]
                      )
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

  def factionColor(name: String): PointColor = {
    dom.console.log(name)
    name match {
      case "Lietuvos socialdemokratų partija" =>
        PointColor("#ff0000")
      case "Lietuvos valstiečių ir žaliųjų sąjunga" =>
        PointColor("#00ff00")
      case "Tėvynės sąjunga - Lietuvos krikščionys demokratai" =>
        PointColor("#0000ff")
      case "Lietuvos Respublikos liberalų sąjūdis" =>
        PointColor("#ffff00")
      case _ => PointColor("#000000")
    }
  }

  class Style extends StyleSheet.Inline {

    import dsl._

    val test = style(
      backgroundColor.red
    )

  }
}
