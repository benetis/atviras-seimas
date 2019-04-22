package services

import diode.ActionResult.ModelUpdate
import diode._
import diode.react.ReactConnector
import me.benetis.shared.api.ApiForFrontend
import me.benetis.shared.{
  DiscussionLength,
  MdsPointWithAdditionalInfo,
  MdsResult,
  TermOfOfficeId
}
import scala.concurrent.ExecutionContext.Implicits.global
import autowire._
import boopickle.Default._
import components.filter.Filter
import org.scalajs.dom

sealed trait GeneralStatsSelectedChart
case object SelectedMdsChart
    extends GeneralStatsSelectedChart
case object SelectedClustering
    extends GeneralStatsSelectedChart

case class RootModel(generalStats: GeneralStatisticsModel)

case class GeneralStatisticsModel(
  selectedGeneralStatsChart: GeneralStatsSelectedChart,
  mdsResult: Option[MdsResult[MdsPointWithAdditionalInfo]],
  mdsFilters: Set[Filter])

case class LoadMdsResult(termOfOfficeId: TermOfOfficeId)
    extends Action
case class MdsResultLoaded(
  mdsResult: Option[MdsResult[MdsPointWithAdditionalInfo]])
    extends Action
case class SetSelectedGeneralStatsTab(
  chart: GeneralStatsSelectedChart)
    extends Action

case class AddMdsFilter(filter: Filter) extends Action
//case class AddMdsFilter(filter: Filter) extends Action

object AppCircuit
    extends Circuit[RootModel]
    with ReactConnector[RootModel] {
  def initialModel =
    RootModel(
      GeneralStatisticsModel(
        SelectedMdsChart,
        None,
        Set.empty
      )
    )

  val vizHandler = new ActionHandler(zoomTo(_.generalStats)) {
    override def handle = {
      case LoadMdsResult(termOfOfficeId) =>
        effectOnly(
          Effect(
            AjaxClient[ApiForFrontend]
              .fetchMdsResults(termOfOfficeId)
              .call()
              .map(MdsResultLoaded)
          )
        )
      case MdsResultLoaded(mdsResult) =>
        updated(value.copy(mdsResult = mdsResult))
      case SetSelectedGeneralStatsTab(tab) =>
        updated(
          value.copy(
            selectedGeneralStatsChart = tab
          )
        )
      case AddMdsFilter(filter: Filter) =>
        if (filter.value.nonEmpty)
          updated(
            value.copy(
              mdsFilters = value.mdsFilters ++ Set(filter)
            )
          )
        else
          noChange

    }
  }

  val actionHandler =
    composeHandlers(vizHandler)
}
