package components.Charts

import components.Charts.ScatterPlot.Props
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.svg_<^.{< => >, ^ => ^^}
import me.benetis.shared.common.Charts.{
  ScatterPlotPointPosition,
  ScatterPoint
}
import org.scalajs.dom.svg.Line
import scala.language.existentials
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry

class ScatterPlot[T <: ScatterPoint] {

  sealed trait Quarter
  case object FirstQuarter  extends Quarter
  case object SecondQuarter extends Quarter
  case object ThirdQuarter  extends Quarter
  case object FourthQuarter extends Quarter

  protected case class DomainPoint(
    x: Double,
    y: Double)
      extends ScatterPoint

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  val component = ScalaComponent
    .builder[Props[T]]("Scatter plot")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props[T], Unit]) {

    val renderQuarters: Vector[Quarter] = Vector(
      FirstQuarter,
      SecondQuarter,
      ThirdQuarter,
      FourthQuarter
    )

    val svgHeight = 500
    val svgWidth  = 500

    def render(
      p: Props[T],
      s: Unit
    ) = {
      <.div(
        styles.scatterContainer,
        "scatter plot",
        >.svg(
          ^^.height := svgHeight,
          ^^.width := svgWidth,
          ^^.viewBox := s"0 0 100 100",
          quarterSplittingLines(p.domain),
          p.data
            .map(
              data =>
                >.g(
                  p.pointToTagMod(
                    data,
                    pointToPointPosition(data, p.domain)
                  )
                )
            )
            .toTagMod
        )
      )
    }

    private def pointToPointPosition(
      point: ScatterPoint,
      domain: ScatterPlot.Domain
    ): ScatterPlotPointPosition = {

      /* Matrix transformations for point
         https://ncase.me/matrix/
       */

      def ratio(
        from: Double,
        to: Double
      ): Double =
        100.0 / (Math.abs(from) + Math.abs(to))

      val ratioX: Double =
        ratio(domain.fromForX, domain.toForX)
      val ratioY: Double =
        ratio(domain.fromForY, domain.toForY)

      val x = (ratioX * point.x) + 50
      val y = (ratioY * -point.y) + 50

      ScatterPlotPointPosition(
        x,
        y
      )
    }

    private def quarterSplittingLines(
      domain: ScatterPlot.Domain
    ): TagMod = {

      sealed trait DomainSize { val value: Double }
      case class DomainXSize(value: Double)
          extends DomainSize
      case class DomainYSize(value: Double)
          extends DomainSize

      val domainXSize = DomainXSize(
        Math.abs(domain.fromForX) + Math
          .abs(domain.toForX)
      )
      val domainYSize = DomainYSize(
        Math.abs(domain.fromForY) + Math
          .abs(domain.toForY)
      )

      val xAxisPoints: Vector[ScatterPlotPointPosition] = {

        val bothQuarters = 2

        val step =
          Math.round(
            domainXSize.value / domain.perHalfOfXAxis
          )

        (0 to domain.perHalfOfXAxis * bothQuarters)
          .map(
            i =>
              pointToPointPosition(
                DomainPoint(i * step, 0),
                domain
              )
          )
          .toVector
      }

      val yAxisLine = >.line(
        ^^.x1 := 50,
        ^^.y1 := 0,
        ^^.x2 := 50,
        ^^.y2 := 100,
        styles.lineStyle
      )

      val xAxisLine = >.line(
        ^^.x1 := 0,
        ^^.y1 := 50,
        ^^.x2 := 100,
        ^^.y2 := 50,
        styles.lineStyle
      )

      val xAxisLinePoints = xAxisPoints.map(point => {
        >.circle(
          ^^.r := 0.5,
          ^^.cx := point.x,
          ^^.cy := point.y
        )
      })

      >.g(
        yAxisLine,
        xAxisLine,
        xAxisLinePoints.toTagMod
      )
    }
  }

  def apply(props: Props[T]) = component(props)

  class Style extends StyleSheet.Inline {
    import dsl._

    val scatterContainer = style(
      display.inlineFlex,
      minHeight(100 px),
      minWidth(100 px),
      flexWrap.wrap,
      flexDirection.column,
      border :=! "1px solid black"
    )

    val lineStyle = style(
      svgStroke(gray),
      svgStrokeWidth := "0.5"
    )
  }
}

object ScatterPlot {

  case class Domain(
    fromForX: Double,
    toForX: Double,
    fromForY: Double,
    toForY: Double,
    perHalfOfXAxis: Int = 5,
    perHalfOfYAxis: Int = 5)

  case class Props[T](
    data: Vector[T],
    pointToTagMod: (T, ScatterPlotPointPosition) => TagMod,
    domain: Domain)

  def apply[T <: ScatterPoint](props: Props[T]) =
    new ScatterPlot().apply(props)
}
