package components

import components.Charts.ScatterPlot
import diode.react.ModelProxy
import japgolly.scalajs.react._
import me.benetis.shared.{
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
import model.FactionColors
import org.scalajs.dom
import japgolly.scalajs.react.vdom.svg_<^.{< => >, ^ => ^^}
import me.benetis.shared.common.Charts.ScatterPlotPointPosition

object HomePage {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Props(
    proxy: ModelProxy[
      Option[MdsResult[MdsPointWithAdditionalInfo]]
    ])

  private val component = ScalaComponent
    .builder[Props]("Home page")
    .stateless
    .renderBackend[Backend]
    .componentDidMount(
      builder =>
        builder.backend.onComponentMount(builder.props)
    )
    .build

  class Backend($ : BackendScope[Props, Unit]) {

    val mdsPoint: (
      MdsPointWithAdditionalInfo,
      ScatterPlotPointPosition
    ) => TagMod =
      (point, position) => {
        >.g(
          >.circle(
            ^^.cx := position.x,
            ^^.cy := position.y,
            ^^.r := 1,
            ^^.fill := FactionColors
              .factionColor(
                point.factionName
              )
              .value,
            >.title(
              s"${point.parliamentMemberName.person_name} ${point.parliamentMemberSurname.person_surname}"
            )
          )
        )
      }

    def onComponentMount(p: Props): Callback = {
      p.proxy.dispatchCB(LoadMdsResult(TermOfOfficeId(8)))
    }

    def render(
      p: Props,
      s: Unit
    ) = {
      <.div(
        <.div("inside render"),
        <.p("Mds data:"),
        p.proxy.value.fold(<.div("Empty MDS"))(
          (result: MdsResult[MdsPointWithAdditionalInfo]) =>
            <.div(
              ScatterPlot(
                ScatterPlot
                  .Props[MdsPointWithAdditionalInfo](
                    data = result.coordinates.value,
                    pointToTagMod = mdsPoint,
                    domain = ScatterPlot
                      .Domain(-30, 30, -50, 50)
                  )
              ),
              FactionLegend(FactionLegend.Props())
            )
        )
      )
    }
  }

  def apply(props: Props) = component(props)

  class Style extends StyleSheet.Inline {

    import dsl._

    val fill = style(
      svgFill := "black"
    )

    val pointText = style(
      fontSize(0.2 em)
    )

  }
}
