package me.benetis.coordinator.computing.MDS

import me.benetis.coordinator.repository.TermOfOfficeRepo
import me.benetis.coordinator.utils.ComputingError
import me.benetis.coordinator.utils.dates.SharedDateDecoders
import me.benetis.shared.{
  MdsPointOnlyXAndY,
  MdsResult,
  TermOfOffice,
  TermOfOfficeId
}
import org.joda.time.{Days, Years}
import scala.concurrent.duration._

object TimeRanges {

  def calculatePeriods(
    termOfOffice: TermOfOffice
  ): Vector[TimeRangeOfMds] = {
    val chosenPeriodDays = 91.25.days
    timeRanges(termOfOffice, chosenPeriodDays)
  }

  private def timeRanges(
    termOfOffice: TermOfOffice,
    chosenRangeDays: Duration
  ): Vector[TimeRangeOfMds] = {
    val dtFrom = SharedDateDecoders.sharedDOToDT(
      termOfOffice.dateFrom.dateFrom
    )

    val dtTo = termOfOffice.dateTo.map(
      d => SharedDateDecoders.sharedDOToDT(d.dateTo)
    )

    def periodsGenerator(nPeriods: Int) = {
      (0 until nPeriods)
        .map(nthPeriod => {
          val from = dtFrom.plusDays(
            nthPeriod * chosenRangeDays.toDays.toInt
          )
          val to = dtFrom.plusDays(
            (nthPeriod + 1) * chosenRangeDays.toDays.toInt
          )
          TimeRangeOfMds(from, to)
        })
        .toVector
    }

    dtTo match {
      case Some(dt) =>
        val diffDays = Days.daysBetween(dtFrom, dt)
        val nPeriods = diffDays
          .dividedBy(
            chosenRangeDays.toDays.toInt
          )
          .getDays

        periodsGenerator(nPeriods)

      case None => //assume 4 yrs
        val diffDays = Days.daysBetween(
          dtFrom,
          dtFrom.plus(Years.years(4))
        )
        val nPeriods = diffDays
          .dividedBy(
            chosenRangeDays.toDays.toInt
          )
          .getDays

        periodsGenerator(nPeriods)
    }
  }
}
