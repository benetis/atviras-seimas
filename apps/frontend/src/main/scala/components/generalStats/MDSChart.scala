package components.generalStats

import cats.data
import components.FactionLegend
import components.charts.ScatterPlot
import components.filter.{DataFilter, Filter}
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import me.benetis.shared.common.Charts.ScatterPlotPointPosition
import me.benetis.shared.{
  MdsPointWithAdditionalInfo,
  MdsResult,
  ParliamentMemberName,
  ParliamentMemberSurname
}
import model.FactionColors
import scalacss.internal.mutable.GlobalRegistry
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import japgolly.scalajs.react.vdom.svg_<^.{< => >, ^ => ^^}
import services.GeneralStatisticsModel

object MDSChart {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Props(
    mdsResult: Option[
      MdsResult[MdsPointWithAdditionalInfo]
    ],
    proxy: ModelProxy[GeneralStatisticsModel])

  private val component = ScalaComponent
    .builder[Props]("Mds chart")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props, Unit]) {

    def filterData(
      filtersToCheck: Set[Filter],
      data: Vector[MdsPointWithAdditionalInfo]
    ): Vector[MdsPointWithAdditionalInfo] = {

      def doValuesMatchFilter(
        filter: Filter,
        valuesToFilter: Vector[String]
      ): Boolean =
        valuesToFilter
          .map(
            value =>
              value
                .toLowerCase()
                .trim
                .contains(filter.value.trim.toLowerCase())
          )
          .fold(false)(_ || _)

      def checkMultipleFilters(
        valuesToFilter: Vector[String]
      ): Boolean =
        filtersToCheck.forall(
          curr =>
            doValuesMatchFilter(
              curr,
              valuesToFilter
            )
        )

      data.filter(point => {
        val fullName = constructName(
          point.parliamentMemberName,
          point.parliamentMemberSurname
        )

        val valuesToFilter =
          Vector(fullName, point.factionName.faction_name)

        if (filtersToCheck.nonEmpty)
          checkMultipleFilters(valuesToFilter)
        else
          true
      })
    }

    def addFilterFromState(
      props: Props,
      data: Vector[MdsPointWithAdditionalInfo]
    ): Vector[MdsPointWithAdditionalInfo] = {
      filterData(props.proxy.value.mdsFilters, data)
    }

    def constructName(
      parliamentMemberName: ParliamentMemberName,
      parliamentMemberSurname: ParliamentMemberSurname
    ): String =
      s"${parliamentMemberName.person_name} ${parliamentMemberSurname.person_surname}"
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
              constructName(
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
      p.mdsResult.fold(<.div("Empty MDS"))(
        (result: MdsResult[MdsPointWithAdditionalInfo]) =>
          <.div(
            styles.container,
            DataFilter(DataFilter.Props(p.proxy)),
            ScatterPlot(
              ScatterPlot
                .Props[MdsPointWithAdditionalInfo](
                  data = addFilterFromState(
                    p,
                    result.coordinates.value
                  ),
                  pointToTagMod = mdsPoint,
                  domain = ScatterPlot
                    .Domain(-30, 30, -50, 50)
                )
            ),
            <.h2(
              styles.title,
              "Kaip panašiai vienas į kitą balsuoją seimo nariai? Kuo arčiau - tuo panašiau"
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
