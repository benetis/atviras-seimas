package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.computing.MDS.MultidimensionalScaling
import me.benetis.coordinator.repository.{
  KMeansRepo,
  MDSRepo,
  VoteRepo
}
import me.benetis.coordinator.utils.ComputingError
import me.benetis.shared._
import me.benetis.shared.encoding.VoteEncoding
import org.joda.time.DateTime
import scala.collection.immutable

object Coordinator extends LazyLogging {
  def apply(computingSettings: ComputingSettings) = {
    computingSettings match {
      case ComputeMDS =>
        val termOfOfficeId = TermOfOfficeId(8)
        MultidimensionalScaling.calculate(
          termOfOfficeId,
          periods = false
        ) match {
          case Right(
              mds: immutable.Seq[Either[
                ComputingError,
                MdsResult[MdsPointOnlyXAndY]
              ]]
              ) =>
            mds.foreach {
              case Right(singleMds) =>
                MDSRepo.insert(singleMds)
              case Left(err) =>
                logger.error(
                  s"MDS of ${mds.length} results size. Error '${err.msg()}'"
                )
            }
          case Left(err) => logger.error(err.msg())
        }
      case ComputeKMeans =>
        val termOfOfficeId = TermOfOfficeId(8)
        val mdsId          = MdsResultId(39)
        KMeansComputing.compute(
          termOfOfficeId,
          VoteEncoding.VoteEncodingE3,
          mdsId
        ) match {
          case Left(value) => logger.error(value.msg())
          case Right(value) =>
            KMeansRepo.insert(value)
        }
      case ComputeMultiFactionsList =>
        VoteRepo.aggregateDistinctFactions(
          TermOfOfficeId(8)
        )
    }
  }
}
