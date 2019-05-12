package me.benetis.coordinator.computing.MDS

import com.typesafe.scalalogging.LazyLogging
import java.io
import me.benetis.coordinator.computing.Utils
import me.benetis.coordinator.repository.{
  ParliamentMemberRepo,
  VoteRepo
}
import me.benetis.coordinator.utils.ComputingError
import me.benetis.shared.encoding.VoteEncoding
import me.benetis.shared.{
  MdsSingleFactionOnly,
  ParliamentMember,
  ParliamentMemberId,
  SharedDateOnly,
  SingleVote,
  TermOfOffice,
  TermOfOfficeDateTo,
  VoteReduced
}
import me.benetis.coordinator.utils.dates.SharedDateDecoders._
import me.benetis.coordinator.utils.dates.SharedDateEncoders._
import me.benetis.shared.encoding.VoteEncoding.VoteEncodingConfig
import org.joda.time.DateTime

case class ProximityMatrix(value: Array[Array[Double]]) {
  override def toString: String = {
    value.map(row => row.mkString(" ")).mkString("\\n")
  }
}

object ProximityMatrix extends LazyLogging {

  def buildMatrices(
    voteEncoding: VoteEncodingConfig,
    termOfOffice: TermOfOffice,
    timeRangeOfMds: Option[Vector[TimeRangeOfMds]],
    singleFactionOnly: MdsSingleFactionOnly
  ): Map[TimeRangeOfMds, ProximityMatrix] = {
    ParliamentMemberRepo.updateTermsSpecificIds(
      termOfOffice
    )

    val members: List[ParliamentMember] =
      if (singleFactionOnly.single_faction_only)
        Utils.membersForSingleFaction(termOfOffice.id)
      else
        ParliamentMemberRepo.listByTermOfOffice(
          termOfOffice.id
        )

    val result =
      timeRangeOfMds match {
        case Some(ranges) =>
          logger.info(
            s"Start building ${ranges.length} proximity matrixes"
          )
          ranges
            .map(
              range =>
                range -> buildMatrix(
                  members = members,
                  termOfOffice = termOfOffice,
                  voteEncoding = voteEncoding,
                  rangeOpt = Some(range)
                )
            )
            .toMap

        case None =>
          logger.info(
            s"Start building proximity matrix"
          )
          Map(
            TimeRangeOfMds(
              sharedDOToDT(termOfOffice.dateFrom.dateFrom),
              termOfOffice.dateTo.fold(
                DateTime.now()
              )(d => sharedDOToDT(d.dateTo))
            ) -> buildMatrix(
              members,
              termOfOffice,
              voteEncoding
            )
          )
      }

    logger.info("Proximity matrix/matrices ready")

    result
  }

  private def buildMatrix(
    members: List[ParliamentMember],
    termOfOffice: TermOfOffice,
    voteEncoding: VoteEncodingConfig,
    rangeOpt: Option[TimeRangeOfMds] = None
  ): ProximityMatrix = {

    var matrix: Matrix =
      Array.ofDim(members.size, members.size)

    val votesForMembers
      : Map[ParliamentMemberId, List[VoteReduced]] =
      rangeOpt match {
        case Some(range) =>
          members
            .map(member => {
              member.personId -> VoteRepo
                .byPersonIdAndRange(
                  member.personId,
                  termOfOffice,
                  range.from.toSharedDateTime(),
                  range.to.toSharedDateTime()
                )
            })
            .toMap
        case None =>
          members
            .map(member => {
              member.personId -> VoteRepo
                .byPersonIdAndTerm(
                  member.personId,
                  termOfOffice
                )
            })
            .toMap

      }

    val cartesian
      : List[(ParliamentMember, ParliamentMember)] =
      members.flatMap(
        member => members.map(m => (member, m))
      )

    ProximityMatrix(
      fillProximityMatrix(
        termOfOffice,
        voteEncoding,
        votesForMembers,
        matrix,
        cartesian
      )
    )
  }

  private def fillProximityMatrix(
    termOfOffice: TermOfOffice,
    voteEncoding: VoteEncodingConfig,
    votesForMembers: Map[ParliamentMemberId, List[
      VoteReduced
    ]],
    matrix: Matrix,
    cartesian: List[(ParliamentMember, ParliamentMember)]
  ): Matrix = {

    cartesian.foreach(pair => {

      if (pair._1.termOfOfficeSpecificId.isEmpty || pair._2.termOfOfficeSpecificId.isEmpty) {
        logger.error(
          "Term of office specific ids must be assigned"
        )
      }

      val pairDistance =
        euclideanDistanceForMemberVotes(
          votesForMembers,
          pair._1,
          pair._2,
          voteEncoding.encode
        )

      matrix(
        pair._1.termOfOfficeSpecificId.get.term_of_office_specific_id
      )(
        pair._2.termOfOfficeSpecificId.get.term_of_office_specific_id
      ) = pairDistance.value
    })

    matrix
  }
  private def euclideanDistanceForMemberVotes(
    votesForMembers: Map[ParliamentMemberId, List[
      VoteReduced
    ]],
    member1: ParliamentMember,
    member2: ParliamentMember,
    encode: SingleVote => Double
  ): EuclideanDistance = {

    def euclidean(
      a: Double,
      b: Double
    ): Double =
      Math.abs(a - b)

    val euclideanSquared = votesForMembers(member1.personId)
      .zip(votesForMembers(member2.personId))
      .par
      .foldLeft(0.0)((prev, curr) => {
        prev + euclidean(
          encode(curr._1.singleVote),
          encode(curr._2.singleVote)
        )
      })

    EuclideanDistance(Math.sqrt(euclideanSquared))
  }

}
