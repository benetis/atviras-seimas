package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.{MDSRepo, VoteRepo}
import me.benetis.coordinator.utils.ComputingError
import me.benetis.shared._
import org.joda.time.DateTime
import smile.mds.MDS

object Coordinator extends LazyLogging {
  def apply(computingSettings: ComputingSettings) = {
    computingSettings match {
      case ComputeMDS =>
        val termOfOfficeId = TermOfOfficeId(8)
        MultidimensionalScaling.calculate(termOfOfficeId) match {
          case Right(mds) => MDSRepo.insert(mds)
          case Left(err)  => logger.error(err.msg())
        }

    }
  }
}
