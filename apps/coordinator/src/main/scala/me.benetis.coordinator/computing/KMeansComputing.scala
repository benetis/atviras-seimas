package me.benetis.coordinator.computing

import me.benetis.coordinator.computing.encoding.VoteEncoding.VoteEncodingT
import me.benetis.coordinator.repository.{
  ParliamentMemberRepo,
  TermOfOfficeRepo,
  VoteRepo
}
import me.benetis.coordinator.utils.{
  ComputingError,
  CustomError
}
import me.benetis.shared.TermOfOfficeId
import smile.clustering._

case class KMeansResult()

object KMeansComputing {
  def compute(
    termOfOfficeId: TermOfOfficeId,
    voteEncoding: VoteEncodingT
  ): Either[ComputingError, KMeansResult] = {

    val termOfOfficeOpt =
      TermOfOfficeRepo.byId(termOfOfficeId)

    termOfOfficeOpt match {
      case Some(termOfOffice) =>
        /**
          * One point - one parliament's votes
          *parameters - how many votes we are looking at
           **
           NameX - 1, 0, 2, 0, ...
          *NameY - 1, 1, 2, -1, ...
          */
//    kmeans()
        val members =
          ParliamentMemberRepo.listByTermOfOffice(
            termOfOfficeId
          )

        val data = members
          .map(member => {
            VoteRepo
              .byPersonIdAndTerm(
                member.personId,
                termOfOffice
              )
              .map(
                voteReduced =>
                  voteEncoding(voteReduced.singleVote)
              )
          })

        println(data.map(_.size))

//        val result =
//          kmeans(
//            data.map(_.toArray).toArray,
//            k = 2,
//            maxIter = 20
//          )

//        println(result.centroids())

        Right(KMeansResult())
      case None =>
        Left(CustomError("Term of office id not found"))
    }
  }
}
