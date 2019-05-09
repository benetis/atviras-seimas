package services

import diode.ActionResult.ModelUpdate
import diode._
import diode.react.ReactConnector
import me.benetis.shared.api.ApiForFrontend
import me.benetis.shared.{
  DiscussionLength,
  KMeansId,
  KMeansResult,
  MdsPointWithAdditionalInfo,
  MdsResult,
  MdsResultId,
  TermOfOfficeId
}
import scala.concurrent.ExecutionContext.Implicits.global
import autowire._
import boopickle.Default._
import components.filter.Filter
import japgolly.scalajs.react.Callback
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
  mdsFilters: Set[Filter],
  kMeansFilters: Set[Filter],
  kMeansSelectedId: Option[KMeansId],
  kMeansResults: Vector[KMeansResult])

case class LoadMdsResult(termOfOfficeId: TermOfOfficeId)
    extends Action
case class MdsResultLoaded(
  mdsResults: Vector[MdsResult[MdsPointWithAdditionalInfo]])
    extends Action

case class LoadKMeansResult(termOfOfficeId: TermOfOfficeId)
    extends Action
case class KMeansResultsLoaded(
  KMeansResult: Vector[KMeansResult])
    extends Action

case class SetSelectedMdsResult(mdsSelectedId: MdsResultId)
    extends Action

case class SetSelectedKMeansResult(kMeansId: KMeansId)
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
        mdsFilters = Set.empty,
        kMeansFilters = Set.empty,
        kMeansSelectedId = None,
        kMeansResults = Vector.empty
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
        val selectFirstIdOpt =
          mdsResult.headOption
            .flatMap(_.id)

        updated(
          value.copy(
            mdsResults = mdsResult,
            mdsSelectedId = selectFirstIdOpt
          )
        )

      case LoadKMeansResult(termOfOfficeId) =>
        effectOnly(
          Effect(
            AjaxClient[ApiForFrontend]
              .fetchKMeansResults(termOfOfficeId)
              .call()
              .map(r => KMeansResultsLoaded(r))
          )
        )

      case SetSelectedKMeansResult(kMeansId: KMeansId) =>
        updated(
          value.copy(
            kMeansSelectedId = Some(kMeansId)
          )
        )

      case KMeansResultsLoaded(kMeansResult) =>
        updated(
          value.copy(
            kMeansResults = kMeansResult,
            kMeansSelectedId =
              kMeansResult.headOption.flatMap(_.id)
          )
        )

      case SetSelectedGeneralStatsTab(tab) =>
        updated(
          value.copy(
            selectedGeneralStatsChart = tab
          )
        )
      case SetSelectedMdsResult(mdsId: MdsResultId) =>
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
