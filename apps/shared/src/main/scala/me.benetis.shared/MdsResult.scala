package me.benetis.shared
import io.getquill.Embedded
import boopickle.Default._

case class EigenValues(value: Array[Double])
case class MDSProportion(value: Array[Double])
case class MDSCoordinates(value: Array[Array[Double]])

case class MdsResult(
    eigenValues: EigenValues,
    proportion: MDSProportion,
    coordinates: MDSCoordinates,
    createdAt: SharedDateTime,
    termOfOfficeId: TermOfOfficeId
) extends Embedded

object MdsResult {
  implicit val pickler: Pickler[MdsResult] = generatePickler[MdsResult]
}
