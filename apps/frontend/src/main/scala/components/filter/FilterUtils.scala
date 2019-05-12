package components.filter

import components.charts.ChartUtils
import components.generalStatsML.KMeansChart.Props
import me.benetis.shared.common.Charts.PointWithParliamentInfo

object FilterUtils {

  def filterData[T <: PointWithParliamentInfo](
    filtersToCheck: Set[Filter],
    data: Vector[T]
  ): Vector[T] = {

    data.filter(point => {
      val fullName = ChartUtils.constructName(
        point.parliamentMemberName,
        point.parliamentMemberSurname
      )

      val valuesToFilter =
        Vector(fullName, point.factionName.faction_name)

      if (filtersToCheck.nonEmpty)
        FilterUtils.checkMultipleFilters(
          filtersToCheck,
          valuesToFilter
        )
      else
        true
    })
  }

  private def doValuesMatchFilter(
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

  private def checkMultipleFilters(
    filtersToCheck: Set[Filter],
    valuesToFilter: Vector[String]
  ): Boolean = {
    filtersToCheck.foldLeft(false)((prev, curr) => {
      prev || doValuesMatchFilter(
        curr,
        valuesToFilter
      )
    })
  }
}
