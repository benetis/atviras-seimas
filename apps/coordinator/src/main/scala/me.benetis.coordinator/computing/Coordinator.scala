package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.VoteRepo
import me.benetis.shared.VoteReduced

object Coordinator extends LazyLogging {
  def apply(computingSettings: ComputingSettings) = {
    computingSettings match {
      case ComputeMDS =>
        val votes: List[VoteReduced] = VoteRepo.list()
        logger.info("Start MDS")
        MDS.buildProximityMatrix(votes)
    }
  }
}
