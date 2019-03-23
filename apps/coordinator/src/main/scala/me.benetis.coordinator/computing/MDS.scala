package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.computing.encoding.VoteEncoding
import me.benetis.coordinator.repository.{ParliamentMemberRepo, VoteRepo}
import me.benetis.coordinator.utils.ComputingError
import me.benetis.shared._

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

    votesEith.map(votes => {
      //MDS per term of office
      //Columns are votes
      //Rows are members

      val columns = votes.size
      val rows    = members.size

      var matrix: Matrix = Array.ofDim[Double](1, 1)

      votesReduced.foreach(voteReduced => {
        val encoded = VoteEncoding.singleVoteEncodedE1(voteReduced.singleVote)
        val voteIdNonNegate =
          voteReduced.id.copy(vote_id = Math.abs(voteReduced.id.vote_id))

        matrix(voteIdNonNegate.vote_id)(voteReduced.personId.person_id) =
          encoded
      })

      ProximityMatrix(matrix)
    })
  }

  private def addTermSpecificIdsToVote(
      list: List[VoteReduced]): List[VoteReduced] = {
    list.zipWithIndex.map {
      case (vote, i) =>
        vote.copy(termSpecificId = Some(VoteTermSpecificId(i)))
    }
  }

}
