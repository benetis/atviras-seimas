package me.benetis.coordinator.computing.MDS

import cats.instances.either._
import cats.instances.vector._
import cats.syntax.traverse._
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.TermOfOfficeRepo
import me.benetis.coordinator.utils.dates.SharedDateEncoders._
import me.benetis.coordinator.utils.{
  ComputingError,
  CustomError,
  DBNotExpectedResult,
  LibraryNotBehavingAsExpected
}
import me.benetis.shared._
import me.benetis.shared.encoding.VoteEncoding.VoteEncodingE1
import org.joda.time.DateTime
import scala.util.Try
import smile.mds._

object MultidimensionalScaling extends LazyLogging {

  def calculate(
    termOfOfficeId: TermOfOfficeId,
    periods: Boolean = true
  ): Either[ComputingError, Vector[
    Either[ComputingError, MdsResult[MdsPointOnlyXAndY]]
  ]] = {

    val termOpt = TermOfOfficeRepo.byId(termOfOfficeId)

    termOpt match {
      case Some(termOfOffice) =>
        val ranges =
          TimeRanges.calculatePeriods(termOfOffice)

        if (periods)
          Right(
            calculateForPeriod(termOfOffice, Some(ranges))
          )
        else Right(calculateForPeriod(termOfOffice, None))
      case None =>
        Left(
          CustomError(
            s"Term of office with id $termOfOfficeId not found"
          )
        )
    }
  }

  private def calculateForPeriod(
    termOfOffice: TermOfOffice,
    timeRangeOfMds: Option[Vector[TimeRangeOfMds]]
  ): Vector[
    Either[ComputingError, MdsResult[MdsPointOnlyXAndY]]
  ] = {

    val voteEncoding = VoteEncodingE1

    logger.info(s"Started MDS with ${termOfOffice.id}")

    ProximityMatrix
      .buildMatrices(
        voteEncoding = voteEncoding,
        termOfOffice = termOfOffice,
        timeRangeOfMds = timeRangeOfMds
      )
      .map((matrix: (TimeRangeOfMds, ProximityMatrix)) => {
        val outputDimensions = 2

        val resultEith: Either[Throwable, MDS] =
          Try(mds(matrix._2.value, outputDimensions)).toEither

        resultEith
          .flatMap(result => {
            val coords
              : Either[ComputingError, MDSCoordinates[
                MdsPointOnlyXAndY
              ]] =
              coordinatesMatrixToMdsPoints(
                result.getCoordinates,
                termOfOffice.id
              )

            coords.map(coords => {
              MdsResult(
                None,
                EigenValues(result.getEigenValues),
                MDSProportion(result.getProportion),
                coords,
                SharedDateTime(DateTime.now().getMillis),
                termOfOffice.id,
                MdsResultFrom(
                  matrix._1.from
                    .toSharedDateTime()
                ),
                MdsResultTo(
                  matrix._1.to.toSharedDateTime()
                ),
                encoding = voteEncoding
              )
            })
          })
          .left
          .flatMap(
            err =>
              Left(
                CustomError(
                  s"Error in Smile MDS calculation: '${err.toString}'"
                )
              )
          )

      })
      .toVector

  }

  private def coordinatesMatrixToMdsPoints(
    matrix: Array[Array[Double]],
    termOfOfficeId: TermOfOfficeId
  ): Either[ComputingError, MDSCoordinates[
    MdsPointOnlyXAndY
  ]] = {

    matrix.zipWithIndex
      .map {
        case (pairArray: Array[Double], id: Int) =>
          val specificId =
            ParliamentMemberTermOfOfficeSpecificId(id)
          //Matrix returned by smile is pairs in array [[x, y], [x, y],...]
          Try {
            val x = pairArray(0)
            val y = pairArray(1)
            (x, y)
          }.toEither.left
            .map(
              t =>
                LibraryNotBehavingAsExpected(t.getMessage)
            )
            .right
            .map(pair => {
              MdsPointOnlyXAndY(
                pair._1,
                pair._2,
                specificId
              )
            })
      }
      .toVector
      .sequence
      .map(points => MDSCoordinates(points))
  }

}
