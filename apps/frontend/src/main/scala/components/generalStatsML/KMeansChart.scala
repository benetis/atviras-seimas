package components.generalStatsML

import components.charts.{
  ChartPointColor,
  ChartUtils,
  ScatterPlot
}
import components.filter.{DataFilter, Filter, FilterUtils}
import components.generalStatsML.MDSChart.Props
import components.{ChartSelectorForKMeans, FactionLegend}
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.~=>
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.svg_<^.{< => >, ^ => ^^}
import me.benetis.shared.common.Charts.{
  PointWithParliamentInfo,
  ScatterPlotPointPosition
}
import me.benetis.shared._
import model.FactionColors
import org.scalajs.dom
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry
import services.GeneralStatisticsModel

object KMeansChart {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Props(
    kMeansResults: Vector[KMeansResult],
    kMeansSelectedId: Option[KMeansId],
    onKMeansResultChange: KMeansId ~=> Callback,
    proxy: ModelProxy[GeneralStatisticsModel])

  private val component = ScalaComponent
    .builder[Props]("KMeans chart")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props, Unit]) {

    def clusterColor(
      p: KMeansPoint,
      totalClusters: KMeansTotalClusters
    ): ChartPointColor = {

      val colors = Map(
        0  -> "#f8f9f9 ",
        1  -> "#5dade2",
        2  -> "#f5b041",
        3  -> "#1abc9c",
        4  -> "#2e4053",
        5  -> "#e74c3c",
        6  -> "#f39c12",
        7  -> "#d35400",
        -1 -> "#d5dbdb"
      )

      ChartPointColor(
        colors.getOrElse(
          p.clusterNumber.cluster_number,
          "#d5dbdb"
        )
      )

    }

    def addFilterFromState(
      props: Props,
      data: Vector[KMeansPoint]
    ): Vector[KMeansPoint] = {
      FilterUtils.filterData(
        props.proxy.value.kMeansFilters,
        data
      )
    }

    val kMeansPoint =
      (totalClusters: KMeansTotalClusters) =>
        (
          point: KMeansPoint,
          position: ScatterPlotPointPosition
        ) => {
          >.g(
            >.circle(
              ^^.cx := position.x,
              ^^.cy := position.y,
              ^^.r := 1,
              ^^.fill := clusterColor(point, totalClusters).value,
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
      p.kMeansResults
        .find(_.id == p.kMeansSelectedId)
        .fold(<.div("Empty KMeans result"))(
          (result: KMeansResult) => {
            <.div(
              styles.container,
              DataFilter(DataFilter.Props(p.proxy)),
              ChartSelectorForKMeans(
                ChartSelectorForKMeans.Props(
                  p.kMeansSelectedId,
                  p.kMeansResults,
                  p.onKMeansResultChange
                )
              ),
              ScatterPlot(
                ScatterPlot
                  .Props[KMeansPoint](
                    data = addFilterFromState(
                      p,
                      result.coordinates.value
                    ),
                    pointToTagMod =
                      kMeansPoint(result.totalClusters),
                    unfilteredData =
                      result.coordinates.value
                  )
              ),
              <.h2(
                styles.title,
                "Į kokias grupes seimo nariai atsiskiria atsižvelgus tik į jų balsavimus?"
              ),
              <.p(
                "Skirtingos spalvos reprezentuoja skirtingą grupę",
                styles.title
              ),
              <.p(
                "(Duomenys nuo 2016.11 iki 2019-04)",
                styles.subtitle
              )
            )
          }
        )
    }
  }

  def apply(props: Props) = component(props)

  class Style extends StyleSheet.Inline {
    import dsl._

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

    val subtitle = title + style(
      fontSize(0.8 em)
    )

    val fill = style(
      svgFill := "black"
    )

    val pointText = style(
      fontSize(0.2 em)
    )

  }
}
