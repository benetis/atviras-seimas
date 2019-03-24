package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.computing.MultidimensionalScaling.Matrix
import me.benetis.coordinator.computing.encoding.VoteEncoding
import me.benetis.coordinator.repository.{
  ParliamentMemberRepo,
  TermOfOfficeRepo,
  VoteRepo
}
import me.benetis.coordinator.utils.{ComputingError, DBNotExpectedResult}
import me.benetis.shared._
import scala.collection.parallel.ParSeq
import scala.collection.parallel.mutable.ParArray
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

  def calculate(termOfOfficeId: TermOfOfficeId): Either[ComputingError, MDS] = {
    buildProximityMatrix(termOfOfficeId).map(matrix => {
      val outputDimensions = 2
      logger.info("Matrix size")
      logger.info(matrix.value.length.toString)
      logger.info(matrix.value(0).length.toString)

      logger.info("Matrix itself:")
      logger.info(matrix.toString)
      mds(matrix.value, outputDimensions)
    })
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
