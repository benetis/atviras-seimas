package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.computing.MDS.MultidimensionalScaling
import me.benetis.coordinator.repository.{
  KMeansRepo,
  MDSRepo,
  MultiFactionItemRepo,
  TermOfOfficeRepo,
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
        val termOfOfficeId    = TermOfOfficeId(8)
        val singleFactionOnly = MdsSingleFactionOnly(true)

        MultidimensionalScaling.calculate(
          termOfOfficeId,
          singleFactionOnly,
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
        val mdsId          = MdsResultId(41)
        val totalClusters  = KMeansTotalClusters(2)
        val kMeansSingleFactionOnly =
          KMeansSingleFactionOnly(true)

        val allEncodings = Vector(
          VoteEncoding.VoteEncodingE1
//          VoteEncoding.VoteEncodingE2,
//          VoteEncoding.VoteEncodingE3
        )

        val mdsOpt = MDSRepo.findById(mdsId)

        mdsOpt.foreach(
          mds =>
            allEncodings
              .map(
                enc =>
                  KMeansComputing.compute(
                    termOfOfficeId,
                    enc,
                    mds,
                    kMeansSingleFactionOnly,
                    totalClusters
                  )
              )
              .foreach {
                case Left(value) =>
                  logger.error(value.msg())
                case Right(value) =>
                  KMeansRepo.insert(value)
              }
        )

      case ComputeMultiFactionsItems =>
        val termOfOffice =
          TermOfOfficeRepo.byId(TermOfOfficeId(8))

        termOfOffice.foreach(
          MultiFactionItemRepo.addFactionsToCurrentList
        )

    }
  }
}
