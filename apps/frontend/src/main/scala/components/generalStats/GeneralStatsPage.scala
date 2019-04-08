package components.generalStats

import components.charts.ScatterPlot
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

object GeneralStatsPage {

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

    def onComponentMount(p: Props): Callback = {
      p.proxy.dispatchCB(LoadMdsResult(TermOfOfficeId(8)))
    }

    def render(
      p: Props,
      s: Unit
    ) = {
      <.div(
        <.div("General stats page"),
        <.p("Mds data:"),
        MDSChart.apply(MDSChart.Props(p.proxy.value))
      )
    }
  }

  def apply(props: Props) = component(props)

  class Style extends StyleSheet.Inline {

    import dsl._

  }
}
