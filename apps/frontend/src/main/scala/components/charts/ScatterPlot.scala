package components.charts

import components.charts.ScatterPlot.Props
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.svg_<^.{< => >, ^ => ^^}
import me.benetis.shared.common.Charts.{
  ScatterPlotPointPosition,
  ScatterPoint
}
import org.scalajs.dom
import org.scalajs.dom.svg.{G, Line}
import scala.language.existentials
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry

class ScatterPlot[T <: ScatterPoint] {

  sealed trait Quarter
  case object FirstQuarter  extends Quarter
  case object SecondQuarter extends Quarter
  case object ThirdQuarter  extends Quarter
  case object FourthQuarter extends Quarter

  private case class DomainPoint(
    x: Double,
    y: Double,
    axis: Axis)
      extends ScatterPoint

  private sealed trait Axis
  private case object XAxis extends Axis
  private case object YAxis extends Axis

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

    lazy val findDomain: Props[T] => ScatterPlot.Domain =
      p => {
        p.domain match {
          case Some(d) => d
          case None =>
            val threshold = 0.3

            def addThreshold(x: Double) =
              x + (threshold * x)
            def subtractThreshold(x: Double) =
              x - (Math.abs(x) * threshold)

            if (p.unfilteredData.isEmpty) {
              ScatterPlot.Domain(
                -5,
                5,
                -5,
                5
              )
            } else {

              val maxX = p.unfilteredData.maxBy(_.x).x
              val minX = p.unfilteredData.minBy(_.x).x

              val maxY = p.unfilteredData.maxBy(_.y).y
              val minY = p.unfilteredData.minBy(_.y).y

              ScatterPlot.Domain(
                subtractThreshold(minX),
                addThreshold(maxX),
                subtractThreshold(minY),
                addThreshold(maxY)
              )
            }

        }
      }

    val svgHeight = 500
    val svgWidth  = 500

    def render(
      p: Props[T],
      s: Unit
    ) = {
      <.div(
        styles.scatterContainer,
        p.backgroundTagMod,
        >.svg(
          ^^.height := svgHeight,
          ^^.width := svgWidth,
          ^^.viewBox := s"0 0 100 100",
          quarterSplittingLines(findDomain(p)),
          p.data
            .map(
              data =>
                >.g(
                  p.pointToTagMod(
                    data,
                    pointToPointPosition(
                      data,
                      findDomain(p)
                    )
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

      val domainPoint: (
        DomainPoint,
        ScatterPlotPointPosition
      ) => TagMod = (
        point: DomainPoint,
        position: ScatterPlotPointPosition
      ) => {

        val pointText = point.axis match {
          case XAxis =>
            s"${point.x.floor}"
          case YAxis =>
            s"${point.y.floor}"
        }

        def emptyIfBothZero(pointText: String) = {
          if (point.x == 0.0 && point.y == 0)
            ""
          else
            pointText
        }

        val textXPosition = point.axis match {
          case XAxis =>
            position.x - 1.5
          case YAxis =>
            position.x + 1.5
        }

        val textYPosition = point.axis match {
          case XAxis =>
            position.y - 1.5
          case YAxis =>
            position.y + 0.8
        }

        >.g(
          >.circle(
            ^^.r := 0.5,
            ^^.cx := position.x,
            ^^.cy := position.y,
            styles.pointColor
          ),
          >.text(
            ^^.x := textXPosition,
            ^^.y := textYPosition,
            emptyIfBothZero(pointText),
            styles.pointText
          )
        )
      }
      def axisPoints(
        axis: Axis
      ): Vector[(DomainPoint, ScatterPlotPointPosition)] = {

        val (
          domainSize,
          perHalfOfAxis,
          domainFrom
        ) = axis match {
          case XAxis =>
            (
              domainXSize,
              domain.perHalfOfXAxis,
              domain.fromForX
            )
          case YAxis =>
            (
              domainYSize,
              domain.perHalfOfYAxis,
              domain.fromForY
            )
        }

        val bothQuarters      = 2
        val totalDomainPoints = bothQuarters * perHalfOfAxis
        val step              = domainSize.value / totalDomainPoints

        val domainPoints = (1 until totalDomainPoints)
          .map(
            i =>
              axis match {
                case XAxis =>
                  DomainPoint(
                    domainFrom + (step * i),
                    0,
                    XAxis
                  )
                case YAxis =>
                  DomainPoint(
                    0,
                    domainFrom + (step * i),
                    YAxis
                  )
              }
          )
          .toVector

        domainPoints.zip(
          domainPoints.map(pointToPointPosition(_, domain))
        )
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

      val xAxisLinePoints = axisPoints(XAxis).map {
        case (point, position) =>
          domainPoint(point, position)
      }

      val yAxisLinePoints = axisPoints(YAxis).map {
        case (point, position) =>
          domainPoint(point, position)
      }

      >.g(
        yAxisLine,
        xAxisLine,
        xAxisLinePoints.toTagMod,
        yAxisLinePoints.toTagMod
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
      flexDirection.column
    )

    val lineStyle = style(
      svgStroke(globalStyles.s.gray1),
      svgStrokeWidth := "0.5"
    )

    val pointText = style(
      fontSize(0.2 em),
      svgFill := globalStyles.s.fontColorOnDark
    )

    val pointColor = style(
      svgFill := globalStyles.s.darkGray
    )

    val backgroundStyles = style()
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
    unfilteredData: Vector[T],
    pointToTagMod: (T, ScatterPlotPointPosition) => TagMod,
    backgroundTagMod: TagMod =
      new ScatterPlot().styles.backgroundStyles,
    domain: Option[Domain] = None)

  def apply[T <: ScatterPoint](props: Props[T]) =
    new ScatterPlot().apply(props)
}
