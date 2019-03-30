package components

import components.ScatterPlot.Props
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.HtmlTagOf
import japgolly.scalajs.react.vdom.PackageBase.VdomAttr
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.svg_<^.{< => >, ^ => ^^}
import me.benetis.shared.common.Charts.ScatterPoint
import org.scalajs.dom
import org.scalajs.dom.svg.Line
import scalacss.internal.mutable.GlobalRegistry
import scala.language.existentials
import scalacss.ScalaCssReact.scalacssStyleaToTagMod

class ScatterPlot[T <: ScatterPoint] {

  sealed trait Quarter
  case object FirstQuarter  extends Quarter
  case object SecondQuarter extends Quarter
  case object ThirdQuarter  extends Quarter
  case object FourthQuarter extends Quarter

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
          quarterSplittingLines().toTagMod
        ),
        p.data.map(p.pointToTagMod).toTagMod
      )
    }

    private def quarterSplittingLines(
    ): Vector[VdomTagOf[Line]] = {
      Vector(
        >.line(
          ^^.x1 := 50,
          ^^.y1 := 0,
          ^^.x2 := 50,
          ^^.y2 := 100,
          styles.lineStyle
        ),
        >.line(
          ^^.x1 := 0,
          ^^.y1 := 50,
          ^^.x2 := 100,
          ^^.y2 := 50,
          styles.lineStyle
        )
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
      border :=! "1px solid black"
    )

    val lineStyle = style(
      svgStroke(red),
      svgStrokeWidth := "0.5"
    )
  }
}

object ScatterPlot {

  case class Domain(
    fromForX: Double,
    toForX: Double,
    fromForY: Double,
    toForY: Double)

  case class Props[T](
    data: Vector[T],
    pointToTagMod: T => TagMod,
    domain: Domain)

  def apply[T <: ScatterPoint](props: Props[T]) =
    new ScatterPlot().apply(props)
}
