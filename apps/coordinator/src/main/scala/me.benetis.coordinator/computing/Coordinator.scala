package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.VoteRepo
import me.benetis.shared.{TermOfOfficeId, VoteReduced}

object Coordinator extends LazyLogging {
  def apply(computingSettings: ComputingSettings) = {
    computingSettings match {
      case ComputeMDS =>
        logger.info("Start MDS")
        val result = MDS.buildProximityMatrix(TermOfOfficeId(8))

        result match {
          case Right(matrix) =>
            logger.info(matrix.toString)
          case Left(err) => logger.error(err.msg())
        }
    }
  }
}
