package me.benetis.coordinator.computing
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.computing.encoding.VoteEncoding
import me.benetis.coordinator.repository.ParliamentMemberRepo
import me.benetis.shared.{SharedDateOnly, TermOfOffice, VoteReduced}

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
  def buildProximityMatrix(termOfOffice: TermOfOffice, votesReduced: List[VoteReduced]): ProximityMatrix = {

    ParliamentMemberRepo.updateTermsSpecificIds()

    //MDS per term of office
    //Columns are votes
    //Rows are members

    val columns = votesReduced.size
    val rows =

    var matrix: Matrix = Array.ofDim[Double]()

    votesReduced.foreach(voteReduced => {
      val encoded = VoteEncoding.singleVoteEncodedE1(voteReduced.singleVote)
      val voteIdNonNegate =
        voteReduced.id.copy(vote_id = Math.abs(voteReduced.id.vote_id))

      matrix(voteIdNonNegate.vote_id)(voteReduced.personId.person_id) = encoded
    })

    ProximityMatrix(matrix)
  }

  def officeTermByDate(date: SharedDateOnly): TermOfOffice = {
    val res = Cache.RepoCache.termOfOffices.find(t => {
      t.dateTo match {
        case Some(termDate) =>
          date > t.dateFrom.dateFrom && date <= termDate.dateTo
        case None => date > t.dateFrom.dateFrom
      }
    })

    res match {
      case Some(x) => x
      case None =>
        logger.error("no dates match term office")
        Cache.RepoCache.termOfOffices.head
    }
  }

}
