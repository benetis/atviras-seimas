package components.styleguide

import components.charts.ScatterPlot
import japgolly.scalajs.react.vdom.html_<^.{<, _}
import japgolly.scalajs.react.vdom.svg_<^.{< => >, ^ => ^^}
import japgolly.scalajs.react.{BackendScope, ScalaComponent}
import me.benetis.shared.common.Charts.{
  ScatterPlotPointPosition,
  ScatterPoint
}
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry

object StyleguidePage {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  private val component = ScalaComponent
    .builder[Unit]("Styleguide page")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Unit, Unit]) {
    def render() = <.div(
      "Styleguide page.",
      scatterPlot()
    )

    case class ScatterDemoPoint(
      x: Double,
      y: Double)
        extends ScatterPoint
    val scatterPlotData = Vector(
      ScatterDemoPoint(0, 0),
      ScatterDemoPoint(10, 10),
      ScatterDemoPoint(10, -10),
      ScatterDemoPoint(0, -10),
      ScatterDemoPoint(-10, -10)
    )

    val demoPoint: (
      ScatterDemoPoint,
      ScatterPlotPointPosition
    ) => TagMod = (point, position) => {
      >.g(
        >.circle(
          ^^.cx := position.x,
          ^^.cy := position.y,
          ^^.r := 1,
          styles.fill
        ),
        >.text(
          ^^.x := position.x - 3,
          ^^.y := position.y - 2,
          s"${point.x},${point.y}",
          styles.pointText
        )
      )
    }

    def scatterPlot(): TagMod = {
      ScatterPlot(
        ScatterPlot
          .Props[ScatterDemoPoint](
            data = scatterPlotData,
            unfilteredData = scatterPlotData,
            pointToTagMod = demoPoint,
            domain = Some(
              ScatterPlot
                .Domain(-30, 30, -50, 50)
            )
          )
      )
    }
  }

  def apply() = component()

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
