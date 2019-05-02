package services

import diode.ActionResult.ModelUpdate
import diode._
import diode.react.ReactConnector
import me.benetis.shared.api.ApiForFrontend
import me.benetis.shared.{
  DiscussionLength,
  MdsPointWithAdditionalInfo,
  MdsResult,
  MdsResultId,
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
  mdsResults: Vector[MdsResult[MdsPointWithAdditionalInfo]],
  mdsSelectedId: Option[MdsResultId],
  mdsFilters: Set[Filter])

case class LoadMdsResult(termOfOfficeId: TermOfOfficeId)
    extends Action
case class MdsResultLoaded(
  mdsResults: Vector[MdsResult[MdsPointWithAdditionalInfo]])
    extends Action

case class SetSelectedMdsResult(mdsSelectedId: MdsResultId)
    extends Action

case class SetSelectedGeneralStatsTab(
  chart: GeneralStatsSelectedChart)
    extends Action

case class AddMdsFilter(filter: Filter)    extends Action
case class RemoveMdsFilter(filter: Filter) extends Action

object AppCircuit
    extends Circuit[RootModel]
    with ReactConnector[RootModel] {
  def initialModel =
    RootModel(
      GeneralStatisticsModel(
        selectedGeneralStatsChart = SelectedMdsChart,
        mdsResults = Vector.empty,
        mdsSelectedId = None,
        mdsFilters = Set.empty
      )
    )

  val vizHandler = new ActionHandler(zoomTo(_.generalStats)) {
    override def handle = {
      case LoadMdsResult(termOfOfficeId) =>
        effectOnly(
          Effect(
            AjaxClient[ApiForFrontend]
              .fetchMdsList(termOfOfficeId)
              .call()
              .map(MdsResultLoaded)
          )
        )
      case MdsResultLoaded(mdsResult) =>
        updated(value.copy(mdsResults = mdsResult))
      case SetSelectedGeneralStatsTab(tab) =>
        updated(
          value.copy(
            selectedGeneralStatsChart = tab
          )
        )
      case SetSelectedMdsResult(mdsId) =>
        updated(
          value.copy(
            mdsSelectedId = Some(mdsId)
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

      case RemoveMdsFilter(filter: Filter) =>
        updated(
          value.copy(
            mdsFilters =
              value.mdsFilters.filter(_ != filter)
          )
        )

    }
  }

  val actionHandler =
    composeHandlers(vizHandler)
}
