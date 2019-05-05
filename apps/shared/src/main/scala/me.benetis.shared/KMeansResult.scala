package me.benetis.shared

import io.getquill.Embedded
import me.benetis.shared.common.Charts.ScatterPoint
import me.benetis.shared.encoding.VoteEncoding.VoteEncodingConfig

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
  parliamentMemberSurname: ParliamentMemberSurname,
  clusterNumber: Int)
    extends Embedded
    with ScatterPoint

case class KMeansPredictedPoints(
  coordinates: MDSCoordinates[KMeansPoint])
    extends Embedded

case class KMeansResult(
  centroids: KMeansCentroids,
  distortion: KMeansDistortion,
  termOfOfficeId: TermOfOfficeId,
  createdAt: SharedDateTime,
  encoding: VoteEncodingConfig,
  coordinates: KMeansPredictedPoints)
    extends Embedded
