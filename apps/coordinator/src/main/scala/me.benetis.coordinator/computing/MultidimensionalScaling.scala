package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
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

case class ProximityMatrix(value: Array[Array[Double]])

object MultidimensionalScaling extends LazyLogging {

  type Matrix = Array[Array[Double]]

  case class EuclideanDistance(value: Double)

  def calculate(termOfOfficeId: TermOfOfficeId): Either[ComputingError, MDS] = {
    buildProximityMatrix(termOfOfficeId).map(matrix => {
      val outputDimensions = 2
      logger.info("Matrix size")
      logger.info(matrix.value.length.toString)
      logger.info(matrix.value(0).length.toString)
      mds(matrix.value, outputDimensions)
    })
  }

  private def buildProximityMatrix(termOfOfficeId: TermOfOfficeId)
    : Either[ComputingError, ProximityMatrix] = {

//    ParliamentMemberRepo.updateTermsSpecificIds()

    val votesEith: Either[ComputingError, List[VoteReduced]] =
      VoteRepo
        .listForTermOfOffice(termOfOfficeId)
        .map(addTermSpecificIdsToVote)

    val members = ParliamentMemberRepo.listByTermOfOffice(termOfOfficeId)

    votesEith.map((votes: List[VoteReduced]) => {

      logger.info("Start building proximity matrix")

      val emptyMatrixElement              = -100.0
      def isEmptyMatrixElement(x: Double) = x == emptyMatrixElement

      var matrix: Matrix =
        Array.fill(members.size, members.size)(emptyMatrixElement)

      //can be optimized due duplicates
      val cartesian: List[(ParliamentMember, ParliamentMember)] =
        members.flatMap(member => members.map(m => (member, m)))

      cartesian.foreach(pair => {

        if (pair._1.termOfOfficeSpecificId.isEmpty || pair._2.termOfOfficeSpecificId.isEmpty) {
          logger.error("Term of office specific ids must be assigned")
        }

        val symmetricMatrixElement =
          matrix(pair._2.termOfOfficeSpecificId.get.term_of_office_specific_id)(
            pair._2.termOfOfficeSpecificId.get.term_of_office_specific_id)

        if (!isEmptyMatrixElement(symmetricMatrixElement)) {
          //Optimize in case its already calculated (since its symmetric)
          matrix(pair._1.termOfOfficeSpecificId.get.term_of_office_specific_id)(
            pair._2.termOfOfficeSpecificId.get.term_of_office_specific_id) =
            symmetricMatrixElement

        } else {
          val pairDistanceEith =
            euclidianDistanceForMemberVotes(termOfOfficeId,
                                            pair._1,
                                            pair._2,
                                            VoteEncoding.singleVoteEncodedE3)

          pairDistanceEith.map(pairDistance => {

            matrix(
              pair._1.termOfOfficeSpecificId.get.term_of_office_specific_id)(
              pair._2.termOfOfficeSpecificId.get.term_of_office_specific_id) =
              pairDistance.value
          })
        }
      })

      logger.info("Proximity matrix ready")

      ProximityMatrix(matrix)
    })
  }

  private def euclidianDistanceForMemberVotes(
      termOfOfficeId: TermOfOfficeId,
      member1: ParliamentMember,
      member2: ParliamentMember,
      encode: SingleVote => Double
  ): Either[ComputingError, EuclideanDistance] = {

    val term = TermOfOfficeRepo.byId(termOfOfficeId)

    term match {
      case Some(termValue) =>
        val votesOfMember1 =
          VoteRepo.byPersonIdAndTerm(member1.personId, termValue)
        val votesOfMember2 =
          VoteRepo.byPersonIdAndTerm(member2.personId, termValue)

        val euclideanSquared = votesOfMember1.par
          .zip(votesOfMember2)
          .par
          .foldLeft(0.0)((prev, curr) => {
            prev + (encode(curr._1.singleVote) - encode(curr._2.singleVote))
          })

        Right(EuclideanDistance(Math.sqrt(euclideanSquared)))
      case None =>
        Left(DBNotExpectedResult(
          s"TermOfOffice with this ID should have been found in the DB. Id: ${termOfOfficeId.term_of_office_id}"))
    }
  }

  private def addTermSpecificIdsToVote(
      list: List[VoteReduced]): List[VoteReduced] = {
    list.zipWithIndex.map {
      case (vote, i) =>
        vote.copy(termSpecificVoteId = Some(VoteTermSpecificId(i)))
    }
  }

  private def getPersonIdForTerm(personId: ParliamentMemberId,
                                 members: List[ParliamentMember])
    : ParliamentMemberTermOfOfficeSpecificId = {
    val p = members.find(_.personId == personId)

    p.flatMap(_.termOfOfficeSpecificId) match {
      case Some(specificId) => specificId
      case None =>
        logger.error("lists given should have specificIds added")
        ParliamentMemberTermOfOfficeSpecificId(-1)
    }

  }

}
