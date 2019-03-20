package me.benetis.coordinator.computing
import me.benetis.coordinator.computing.encoding.VoteEncoding
import me.benetis.shared.VoteReduced

case class ProximityMatrix(value: Array[Array[Double]]) {
  override def toString: String = {
    value.map(row => row.mkString(" ")).mkString("\\n")
  }
}

object MDS {

  type Matrix = Array[Array[Double]]

  /*
   * Rows - votes
   * Columns - parliament members
   */
  def buildProximityMatrix(votesReduced: List[VoteReduced]): ProximityMatrix = {

    var matrix: Matrix = Array.ofDim[Double](votesReduced.size + 1200, 80000)

    votesReduced.foreach(voteReduced => {
      val encoded = VoteEncoding.singleVoteEncodedE1(voteReduced.singleVote)
      val voteIdNonNegate =
        voteReduced.id.copy(vote_id = Math.abs(voteReduced.id.vote_id))

      matrix(voteIdNonNegate.vote_id)(voteReduced.personId.person_id) = encoded
    })

    ProximityMatrix(matrix)
  }

  //1. Scrapinam visus seimo narius, kada kuris buvo kurioje kadencijoje
  //2. Pagal data skaiciuojam kuri kadencija
  //3. Kiekvienam tos kadencijos seimo nariui priskiriam po id (0-150~)
  //Limitacijos: tik vienoje kadencijoje MDS'as

//  def personIndiceByOfficeTerm()
}
