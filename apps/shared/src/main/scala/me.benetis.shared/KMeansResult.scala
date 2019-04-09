package me.benetis.shared

import io.getquill.Embedded
import me.benetis.shared.common.Charts.ScatterPoint

case class KMeansCentroids(centroids: Array[Array[Double]])
    extends Embedded

case class KMeansDistortion(distortion: Double)
    extends Embedded

case class KMeansPoint(
  x: Double,
  y: Double,
  factionName: ParliamentMemberFactionName,
  parliamentMemberId: ParliamentMemberId,
  parliamentMemberName: ParliamentMemberName,
  parliamentMemberSurname: ParliamentMemberSurname)
    extends Embedded
    with ScatterPoint

case class KMeansPredictedCoordinates(
  coordinates: Vector[KMeansPoint])
    extends Embedded

case class KMeansResult(
  centroids: KMeansCentroids,
  distortion: KMeansDistortion,
  termOfOfficeId: TermOfOfficeId,
  createdAt: SharedDateTime,
  encoding: VoteEncoding,
  coordinates: KMeansPredictedCoordinates)
    extends Embedded
