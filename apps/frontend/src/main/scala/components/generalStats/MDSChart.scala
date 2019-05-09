package components.generalStats

import components.{
  ChartDateRange,
  ChartSelectorForKMeans,
  FactionLegend
}
import components.charts.{ChartUtils, ScatterPlot}
import components.filter.{DataFilter, Filter, FilterUtils}
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.~=>
import japgolly.scalajs.react.vdom.html_<^._
import me.benetis.shared.common.Charts.ScatterPlotPointPosition
import me.benetis.shared.{
  MdsPointWithAdditionalInfo,
  MdsResult,
  MdsResultId,
  ParliamentMemberName,
  ParliamentMemberSurname
}
import model.FactionColors
import scalacss.internal.mutable.GlobalRegistry
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import japgolly.scalajs.react.vdom.svg_<^.{< => >, ^ => ^^}
import org.scalajs.dom
import services.GeneralStatisticsModel

object MDSChart {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Props(
    mdsResults: Vector[
      MdsResult[MdsPointWithAdditionalInfo]
    ],
    mdsSelectedId: Option[MdsResultId],
    onMdsResultChange: MdsResultId ~=> Callback,
    proxy: ModelProxy[GeneralStatisticsModel])

  private val component = ScalaComponent
    .builder[Props]("Mds chart")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props, Unit]) {

    def addFilterFromState(
      props: Props,
      data: Vector[MdsPointWithAdditionalInfo]
    ): Vector[MdsPointWithAdditionalInfo] = {
      FilterUtils.filterData(
        props.proxy.value.mdsFilters,
        data
      )
    }

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
                point.factionName,
                Some(point.parliamentMemberId)
              )
              .value,
            >.title(
              ChartUtils.constructName(
                point.parliamentMemberName,
                point.parliamentMemberSurname
              )
            )
          )
        )
      }

    def render(
      p: Props,
      s: Unit
    ) = {

      p.mdsResults
        .find(_.id == p.mdsSelectedId)
        .fold(<.div("Empty MDS"))(
          (result: MdsResult[MdsPointWithAdditionalInfo]) => {
            <.div(
              styles.container,
              DataFilter(DataFilter.Props(p.proxy)),
              ChartDateRange(
                ChartDateRange.Props(
                  p.mdsSelectedId,
                  p.mdsResults,
                  p.onMdsResultChange
                )
              ),
              ScatterPlot(
                ScatterPlot
                  .Props[MdsPointWithAdditionalInfo](
                    data = addFilterFromState(
                      p,
                      result.coordinates.value
                    ),
                    unfilteredData =
                      result.coordinates.value,
                    pointToTagMod = mdsPoint
                  )
              ),
              <.h2(
                styles.title,
                "Kaip panašiai vienas į kitą balsuoją seimo nariai? Kuo arčiau - tuo panašiau"
              ),
              FactionLegend(FactionLegend.Props()),
              <.div(
                styles.dataFromTo
              )
            )
          }
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

    val container = style(
      display.flex,
      flexDirection.column,
      alignItems.center
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
