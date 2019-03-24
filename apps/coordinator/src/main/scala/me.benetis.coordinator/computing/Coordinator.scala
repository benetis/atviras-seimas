package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.VoteRepo
import me.benetis.coordinator.utils.ComputingError
import me.benetis.shared.{TermOfOfficeId, VoteReduced}
import smile.mds.MDS

object Coordinator extends LazyLogging {
  def apply(computingSettings: ComputingSettings) = {
    computingSettings match {
      case ComputeMDS =>
        logger.info("Start MultidimensionalScaling")
        val result: Either[ComputingError, MDS] =
          MultidimensionalScaling.calculate(TermOfOfficeId(8))

        logger.info("MDS finished")

        def toString(matrix: Array[Array[Double]]): String = {
          matrix.map(row => row.mkString(" ")).mkString("\\n")
        }

        result match {
          case Right(mds) =>
            logger.info(mds.getEigenValues.mkString(" "))
            logger.info("Coordinates of MDS")
            logger.info(toString(mds.getCoordinates))
          case Left(err) => logger.error(err.msg())
        }
    }
  }
}
