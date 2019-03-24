package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.{MDSRepo, VoteRepo}
import me.benetis.coordinator.utils.ComputingError
import me.benetis.shared.{MDSResults, TermOfOfficeId, VoteReduced}
import smile.mds.MDS

object Coordinator extends LazyLogging {
  def apply(computingSettings: ComputingSettings) = {
    computingSettings match {
      case ComputeMDS =>
        val termOfOfficeId = TermOfOfficeId(8)

        logger.info(s"Started MDS with $termOfOfficeId")

        val result: Either[ComputingError, MDS] =
          MultidimensionalScaling.calculate(termOfOfficeId)

        logger.info("MDS calculations finished")

        result match {
          case Right(mds) =>
            MDSRepo.insert(
              MDSResults(
                mds.getEigenValues,
                mds.getProportion,
                mds.getCoordinates,
                termOfOfficeId
              ))

          case Left(err) => logger.error(err.msg())
        }
    }
  }
}
