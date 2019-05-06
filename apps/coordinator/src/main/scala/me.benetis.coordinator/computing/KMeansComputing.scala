package me.benetis.coordinator.computing

import me.benetis.coordinator.computing.MDS.{
  AdditionalInfo,
  MultidimensionalScaling
}
import me.benetis.shared.encoding.VoteEncoding.VoteEncodingConfig
import me.benetis.coordinator.repository.{
  MDSRepo,
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
    mdsId: MdsResultId
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

        votesIds.flatMap(allVotesIds => {
          val data: Map[ParliamentMemberId, Array[Double]] =
            members
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

          val totalClusters = 9

          val model = kmeans(
            data.values.toArray,
            k = totalClusters,
            maxIter = 20
          )

          val predictedEith = predictByModel(
            model = model,
            members = members,
            mdsResultId = mdsId,
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
                KMeansTotalClusters(totalClusters)
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
    mdsResultId: MdsResultId,
    data: Map[ParliamentMemberId, Array[Double]]
  ): Either[ComputingError, MDSCoordinates[KMeansPoint]] = {

    def getFromPredictions(
      parliamentMemberId: ParliamentMemberId
    ): Option[Int] = {
      predictions.get(parliamentMemberId)
    }

    lazy val mds =
      MDSRepo
        .findById(mdsResultId)
        .flatMap(
          (res: MdsResult[MdsPointOnlyXAndY]) => {
            AdditionalInfo
              .transformToAdditionalInfo(res, members)
              .map(coords => res.copy(coordinates = coords)) match {
              case Left(value)  => None
              case Right(value) => Some(value)
            }
          }
        )

    lazy val predictions: Map[ParliamentMemberId, Int] =
      data.map(tupl => {
        tupl._1 -> model.predict(tupl._2)
      })

    mds.map(mdsRes => {
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
    }) match {
      case Some(value) =>
        Right(MDSCoordinates(value))
      case None =>
        Left(CustomError("Mds by given id not found"))
    }

  }
}
