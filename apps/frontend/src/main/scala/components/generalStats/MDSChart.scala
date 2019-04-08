package components.generalStats

import components.FactionLegend
import components.charts.ScatterPlot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import me.benetis.shared.common.Charts.ScatterPlotPointPosition
import me.benetis.shared.{
  MdsPointWithAdditionalInfo,
  MdsResult
}
import model.FactionColors
import scalacss.internal.mutable.GlobalRegistry
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import japgolly.scalajs.react.vdom.svg_<^.{< => >, ^ => ^^}

object MDSChart {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Props(
    mdsResult: Option[
      MdsResult[MdsPointWithAdditionalInfo]
    ])

  private val component = ScalaComponent
    .builder[Props]("Mds chart")
    .stateless
    .renderBackend[Backend]
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

    def render(
      p: Props,
      s: Unit
    ) = {
      p.mdsResult.fold(<.div("Empty MDS"))(
        (result: MdsResult[MdsPointWithAdditionalInfo]) =>
          <.div(
            <.h2(
              styles.title,
              "Kaip panašiai vienas į kitą balsuoją seimo nariai? Kuo arčiau - tuo panašiau"
            ),
            ScatterPlot(
              ScatterPlot
                .Props[MdsPointWithAdditionalInfo](
                  data = result.coordinates.value,
                  pointToTagMod = mdsPoint,
                  domain = ScatterPlot
                    .Domain(-30, 30, -50, 50)
                )
            ),
            FactionLegend(FactionLegend.Props()),
            <.div(
              styles.dataFromTo,
              "Duomenys nuo 2016.11.01 iki 2019.03.28"
            )
          )
      )
    }
  }

  def apply(props: Props) = component(props)

  class Style extends StyleSheet.Inline {
    import dsl._

    val dataFromTo = style(
      marginTop(10 px),
      textAlign.center,
      color(globalStyles.s.fontColorOnDark2)
    )

    val title = style(
      textAlign.center,
      marginTop(5 px),
      marginBottom(10 px)
    )

    val fill = style(
      svgFill := "black"
    )

    val pointText = style(
      fontSize(0.2 em)
    )

  }
}
