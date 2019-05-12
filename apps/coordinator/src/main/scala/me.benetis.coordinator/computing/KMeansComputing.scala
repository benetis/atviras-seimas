package me.benetis.coordinator.computing

import me.benetis.coordinator.computing.MDS.{
  AdditionalInfo,
  MultidimensionalScaling
}
import me.benetis.shared.encoding.VoteEncoding.VoteEncodingConfig
import me.benetis.coordinator.repository.{
  MDSRepo,
  MultiFactionItemRepo,
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
  KMeansClusterNumber,
  KMeansDistortion,
  KMeansPoint,
  KMeansResult,
  KMeansSingleFactionOnly,
  KMeansTotalClusters,
  MDSCoordinates,
  MdsPointOnlyXAndY,
  MdsPointWithAdditionalInfo,
  MdsResult,
  MdsResultId,
  ParliamentMember,
  ParliamentMemberId,
  SharedDateTime,
  TermOfOffice,
  TermOfOfficeId,
  VoteId,
  VoteReduced
}
import org.joda.time.DateTime
import smile.clustering._

object KMeansComputing {
  def compute(
    termOfOfficeId: TermOfOfficeId,
    voteEncoding: VoteEncodingConfig,
    mdsResult: MdsResult[MdsPointOnlyXAndY],
    singleFactionOnly: KMeansSingleFactionOnly,
    totalClusters: KMeansTotalClusters
  ): Either[ComputingError, KMeansResult] = {

    val termOfOfficeOpt =
      TermOfOfficeRepo.byId(termOfOfficeId)

    termOfOfficeOpt
      .map(termOfOffice => {
        val membersI: List[ParliamentMember] =
          if (singleFactionOnly.single_faction_only)
            Utils.membersForSingleFaction(termOfOfficeId)
          else
            ParliamentMemberRepo.listByTermOfOffice(
              termOfOfficeId
            )

        val votesIds = VoteRepo
          .listForTermOfOffice(termOfOfficeId)
          .map(_.groupBy(v => v.id))
          .map(_.values.flatten.toArray.map(_.id))

        votesIds.flatMap(allVotesIds => {
          val data: Map[ParliamentMemberId, Array[Double]] =
            membersI
              .map(
                m =>
                  m.personId -> transformToTrainingRow(
                    voteEncoding,
                    termOfOffice,
                    m,
                    allVotesIds
                  )
              )
              .toMap

          val model = kmeans(
            data.values.toArray,
            k = totalClusters.total_clusters,
            maxIter = 20
          )

          val predictedEith = predictByModel(
            model = model,
            members = membersI,
            mdsResult = mdsResult,
            data = data
          )

          predictedEith.map(
            predicted =>
              KMeansResult(
                None,
                KMeansCentroids(Array.empty), /* Too big */
                KMeansDistortion(model.distortion()),
                termOfOfficeId,
                SharedDateTime(DateTime.now().getMillis),
                voteEncoding,
                predicted,
                totalClusters,
                singleFactionOnly
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
    voteEncoding: VoteEncodingConfig,
    termOfOffice: TermOfOffice,
    member: ParliamentMember,
    allVotesIds: Array[VoteId]
  ): Array[Double] = {
    val personVotes: List[VoteReduced] = VoteRepo
      .byPersonIdAndTerm(
        member.personId,
        termOfOffice
      )

    addMissingVotes(
      personVotes,
      allVotesIds
    ).map(
      voteReduced =>
        voteEncoding.encode(voteReduced.singleVote)
    )
  }

  def addMissingVotes(
    personVotes: List[VoteReduced],
    allTermIds: Array[VoteId]
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
    mdsResult: MdsResult[MdsPointOnlyXAndY],
    data: Map[ParliamentMemberId, Array[Double]]
  ): Either[ComputingError, MDSCoordinates[KMeansPoint]] = {
    val mds = AdditionalInfo
      .transformToAdditionalInfo(mdsResult, members)
      .map(coords => mdsResult.copy(coordinates = coords))

    val predictions: Map[ParliamentMemberId, Int] =
      data.map(tupl => {
        tupl._1 -> model.predict(tupl._2)
      })

    def getFromPredictions(
      parliamentMemberId: ParliamentMemberId
    ): Option[Int] = {
      predictions.get(parliamentMemberId)
    }

    mds
      .map(mdsRes => {
        mdsRes.coordinates.value.map(point => {
          KMeansPoint(
            point.x,
            point.y,
            point.factionName,
            point.parliamentMemberId,
            point.parliamentMemberName,
            point.parliamentMemberSurname,
            KMeansClusterNumber(
              getFromPredictions(point.parliamentMemberId)
                .getOrElse(-1)
            )
          )
        })
      })
      .map(MDSCoordinates(_))

  }
}
