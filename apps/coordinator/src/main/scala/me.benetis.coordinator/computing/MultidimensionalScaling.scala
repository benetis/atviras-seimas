package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.computing.Coordinator.logger
import me.benetis.coordinator.computing.MultidimensionalScaling.Matrix
import me.benetis.coordinator.computing.encoding.VoteEncoding
import me.benetis.coordinator.repository.{
  MDSRepo,
  ParliamentMemberRepo,
  TermOfOfficeRepo,
  VoteRepo
}
import me.benetis.coordinator.utils.{
  ComputingError,
  DBNotExpectedResult,
  LibraryNotBehavingAsExpected
}
import me.benetis.shared.Common.Point
import me.benetis.shared._
import org.joda.time.DateTime
import scala.collection.parallel.ParSeq
import scala.collection.parallel.mutable.ParArray
import scala.util.Try
import scalaz.zio.{Fiber, UIO}
import smile.mds._

case class ProximityMatrix(value: Array[Array[Double]]) {
  override def toString: String = {
    value.map(row => row.mkString(" ")).mkString("\\n")
  }
}

object MultidimensionalScaling extends LazyLogging {
  type Matrix = Array[Array[Double]]

  case class EuclideanDistance(value: Double)

  def calculate(
      termOfOfficeId: TermOfOfficeId): Either[ComputingError, MdsResult] = {
    logger.info(s"Started MDS with $termOfOfficeId")

    buildProximityMatrix(termOfOfficeId).flatMap(matrix => {
      val outputDimensions = 2
      logger.info("Matrix size")
      logger.info(matrix.value.length.toString)
      logger.info(matrix.value(0).length.toString)

      val result = mds(matrix.value, outputDimensions)

      logger.info("MDS calculations finished")

      val coords: Either[ComputingError, MDSCoordinates] =
        coordinatesMatrixToPairs(result.getCoordinates)

      coords.map(coords => {
        MdsResult(
          EigenValues(result.getEigenValues),
          MDSProportion(result.getProportion),
          coords,
          SharedDateTime(DateTime.now().getMillis),
          termOfOfficeId
        )
      })
    })
  }

  private def coordinatesMatrixToPairs(
      matrix: Array[Array[Double]]): Either[ComputingError, MDSCoordinates] = {

    //Matrix returned by smile is pairs in array [[x, y], [x, y],...]

    Try(matrix.map(pairArray => Point(pairArray(0), pairArray(1)))).toEither.left
      .map(t => LibraryNotBehavingAsExpected(t.getMessage))
      .right
      .map(MDSCoordinates(_))
  }

  private def buildProximityMatrix(termOfOfficeId: TermOfOfficeId)
    : Either[ComputingError, ProximityMatrix] = {

    val termOpt = TermOfOfficeRepo.byId(termOfOfficeId)

    termOpt match {
      case Some(term) =>
        ParliamentMemberRepo.updateTermsSpecificIds(term)

        val members = ParliamentMemberRepo.listByTermOfOffice(termOfOfficeId)

        logger.info("Start building proximity matrix")

        var matrix: Matrix =
          Array.ofDim(members.size, members.size)

        val votesForMembers: Map[ParliamentMemberId, List[VoteReduced]] =
          members
            .map(member => {
              member.personId -> VoteRepo
                .byPersonIdAndTerm(member.personId, term)
            })
            .toMap

        val cartesian: List[(ParliamentMember, ParliamentMember)] =
          members.flatMap(member => members.map(m => (member, m)))

        fillProximityMatrix(term, votesForMembers, matrix, cartesian)

        logger.info("Proximity matrix ready")

        Right(ProximityMatrix(matrix))
      case None =>
        Left(DBNotExpectedResult(
          s"TermOfOffice with this ID should have been found in the DB. Id: ${termOfOfficeId.term_of_office_id}"))
    }
  }

  private def fillProximityMatrix(
      termOfOffice: TermOfOffice,
      votesForMembers: Map[ParliamentMemberId, List[VoteReduced]],
      matrix: Matrix,
      cartesian: List[(ParliamentMember, ParliamentMember)]): Unit = {

    cartesian.foreach(pair => {

      if (pair._1.termOfOfficeSpecificId.isEmpty || pair._2.termOfOfficeSpecificId.isEmpty) {
        logger.error("Term of office specific ids must be assigned")
      }

      val pairDistance =
        euclideanDistanceForMemberVotes(votesForMembers,
                                        pair._1,
                                        pair._2,
                                        VoteEncoding.singleVoteEncodedE3)

      matrix(pair._1.termOfOfficeSpecificId.get.term_of_office_specific_id)(
        pair._2.termOfOfficeSpecificId.get.term_of_office_specific_id) =
        pairDistance.value
    })
  }
  private def euclideanDistanceForMemberVotes(
      votesForMembers: Map[ParliamentMemberId, List[VoteReduced]],
      member1: ParliamentMember,
      member2: ParliamentMember,
      encode: SingleVote => Double
  ): EuclideanDistance = {

    def euclidean(a: Double, b: Double): Double = Math.abs(a - b)

    val euclideanSquared = votesForMembers(member1.personId)
      .zip(votesForMembers(member2.personId))
      .par
      .foldLeft(0.0)((prev, curr) => {
        prev + euclidean(encode(curr._1.singleVote), encode(curr._2.singleVote))
      })

    EuclideanDistance(Math.sqrt(euclideanSquared))
  }

}
