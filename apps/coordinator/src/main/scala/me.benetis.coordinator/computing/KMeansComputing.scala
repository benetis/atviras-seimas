package me.benetis.coordinator.computing

import me.benetis.shared.encoding.VoteEncoding.VoteEncodingT
import me.benetis.coordinator.repository.{
  ParliamentMemberRepo,
  TermOfOfficeRepo,
  VoteRepo
}
import me.benetis.coordinator.utils.{
  ComputingError,
  CustomError
}
import me.benetis.shared.{
  DidntVote,
  KMeansCentroids,
  KMeansDistortion,
  KMeansPoint,
  KMeansPredictedCoordinates,
  KMeansResult,
  ParliamentMember,
  SharedDateTime,
  TermOfOffice,
  TermOfOfficeId,
  VoteEncoding1,
  VoteId,
  VoteReduced
}
import org.joda.time.DateTime
import smile.clustering._

object KMeansComputing {
  def compute(
    termOfOfficeId: TermOfOfficeId,
    voteEncoding: VoteEncodingT
  ): Either[ComputingError, KMeansResult] = {

    val termOfOfficeOpt =
      TermOfOfficeRepo.byId(termOfOfficeId)

    termOfOfficeOpt
      .map(termOfOffice => {
        val members: List[ParliamentMember] =
          ParliamentMemberRepo.listByTermOfOffice(
            termOfOfficeId
          )

        val votesIds = VoteRepo
          .listForTermOfOffice(termOfOfficeId)
          .map(_.groupBy(v => v.id))
          .map(_.values.flatten.toArray.map(_.id))

        votesIds.map(allVotesIds => {
          val data = members.map(
            m =>
              transformToTrainingRow(
                voteEncoding,
                termOfOffice,
                m,
                allVotesIds
              )
          )

          val model = kmeans(
            data.toArray,
            k = 2,
            maxIter = 20
          )

          KMeansResult(
            KMeansCentroids(Array.empty),
            KMeansDistortion(model.distortion()),
            termOfOfficeId,
            SharedDateTime(DateTime.now().getMillis),
            VoteEncoding1,
            KMeansPredictedCoordinates(
              predictByModel(
                model,
                members,
                voteEncoding,
                termOfOffice,
                allVotesIds
              )
            )
          )
        })
      }) match {
      case Some(v) => v
      case None =>
        Left(CustomError("Term of office id not found"))
    }
  }

  private def transformToTrainingRow(
    voteEncoding: VoteEncodingT,
    termOfOffice: TermOfOffice,
    member: ParliamentMember,
    allVotesIds: Array[VoteId]
  ) = {
    val personVotes: List[VoteReduced] = VoteRepo
      .byPersonIdAndTerm(
        member.personId,
        termOfOffice
      )

    addMissingVotes(
      personVotes,
      allVotesIds,
      voteEncoding
    ).map(
      voteReduced => voteEncoding(voteReduced.singleVote)
    )
  }

  def addMissingVotes(
    personVotes: List[VoteReduced],
    allTermIds: Array[VoteId],
    encodingT: VoteEncodingT
  ): Array[VoteReduced] = {

    val missingIds = allTermIds.diff(personVotes.map(_.id))

    val personVote = personVotes.head

    missingIds.map(
      id =>
        VoteReduced(
          id,
          DidntVote,
          personVote.personId,
          personVote.dateTime,
          personVote.termSpecificVoteId
        )
    ) ++ personVotes

  }

  def predictByModel(
    model: KMeans,
    members: List[ParliamentMember],
    voteEncoding: VoteEncodingT,
    termOfOffice: TermOfOffice,
    allVoteIds: Array[VoteId]
  ): Vector[KMeansPoint] = {
    members
      .map(
        member => {
          model.predict(
            transformToTrainingRow(
              voteEncoding,
              termOfOffice,
              member,
              allVoteIds
            )
          )
          KMeansPoint(
            0,
            0,
            member.factionName,
            member.personId,
            member.name,
            member.surname
          )
        }
      )
      .toVector
  }
}
