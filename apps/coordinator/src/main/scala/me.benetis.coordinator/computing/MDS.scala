package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.computing.encoding.VoteEncoding
import me.benetis.coordinator.repository.{ParliamentMemberRepo, VoteRepo}
import me.benetis.coordinator.utils.ComputingError
import me.benetis.shared._
import scalaz.zio.{Fiber, UIO}

case class ProximityMatrix(value: Array[Array[Double]]) {
  override def toString: String = {
    value.map(row => row.mkString(" ")).mkString("\\n")
  }
}

object MDS extends LazyLogging {

  case class PersonIndice(indice: Int)

  type Matrix = Array[Array[Double]]

  /*
   * Rows - votes
   * Columns - parliament members
   */
  def buildProximityMatrix(termOfOfficeId: TermOfOfficeId)
    : Either[ComputingError, ProximityMatrix] = {

    ParliamentMemberRepo.updateTermsSpecificIds()

    val votesEith: Either[ComputingError, List[VoteReduced]] =
      VoteRepo
        .listForTermOfOffice(termOfOfficeId)
        .map(addTermSpecificIdsToVote)

    val members = ParliamentMemberRepo.listByTermOfOffice(termOfOfficeId)

    votesEith.map((votes: List[VoteReduced]) => {
      //MDS per term of office
      //Columns are votes
      //Rows are members

      val columns = votes.size
      val rows    = members.size

      var matrix: Matrix = Array.ofDim[Double](rows, columns)

      logger.info(s"Matrix created, $rows x $columns")

      votes.par.foreach(voteReduced => {
        val encoded: Double =
          VoteEncoding.singleVoteEncodedE3(voteReduced.singleVote)

        matrix(
          getPersonIdForTerm(voteReduced.personId, members).term_of_office_specific_id)(
          voteReduced.termSpecificVoteId.get.vote_term_specific_id) = encoded
      })

      logger.info("MDS ready")

      ProximityMatrix(matrix)
    })
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
