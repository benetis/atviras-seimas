package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.VoteRepo
import me.benetis.shared.{TermOfOfficeId, VoteReduced}

object Coordinator extends LazyLogging {
  def apply(computingSettings: ComputingSettings) = {
    computingSettings match {
      case ComputeMDS =>
        logger.info("Start MDS")
        MDS.buildProximityMatrix(TermOfOfficeId(8))
    }
  }
}
