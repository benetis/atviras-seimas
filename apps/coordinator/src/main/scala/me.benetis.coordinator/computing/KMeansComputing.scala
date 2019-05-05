package me.benetis.coordinator.computing

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
  KMeansDistortion,
  KMeansPoint,
  KMeansPredictedPoints,
  KMeansResult,
  MDSCoordinates,
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

        votesIds.map(allVotesIds => {
          val data: Map[ParliamentMember, Array[Double]] =
            members
              .map(
                m =>
                  m -> transformToTrainingRow(
                    voteEncoding,
                    termOfOffice,
                    m,
                    allVotesIds
                  )
              )
              .toMap

          val model = kmeans(
            data.values.toArray,
            k = 3,
            maxIter = 20
          )

          KMeansResult(
            KMeansCentroids(Array.empty), /* Too big */
            KMeansDistortion(model.distortion()),
            termOfOfficeId,
            SharedDateTime(DateTime.now().getMillis),
            voteEncoding,
            KMeansPredictedPoints(
              predictByModel(
                model = model,
                mdsResultId = mdsId,
                data = data
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
    mdsResultId: MdsResultId,
    data: Map[ParliamentMember, Array[Double]]
  ): Option[MDSCoordinates[KMeansPoint]] = {

    def getFromPredictions(
      parliamentMemberId: ParliamentMemberId
    ): Int = {
      predictions.get()
    }

    lazy val mds
      : Option[MdsResult[MdsPointWithAdditionalInfo]] =
      MDSRepo.findById(mdsResultId)

    lazy val predictions: Map[ParliamentMember, Int] =
      data.map((tupl) => {
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
          1
        )
      })
    })

//    apjungti mds ir predictions
//kmeans results turi grazinti lista kur paaiskinti clusteriai(? ) arba ne

  }
}
