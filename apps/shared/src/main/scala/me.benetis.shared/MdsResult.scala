package me.benetis.shared
import io.getquill.Embedded

import boopickle.Default._

case class EigenValues(value: Array[Double]) extends Embedded

object EigenValues {
  import boopickle.Default._

  implicit val pickler: Pickler[EigenValues] = generatePickler[EigenValues]
}

object MDSProportion {
  implicit val pickler: Pickler[MDSProportion] = generatePickler[MDSProportion]
}

object MDSCoordinates {
  implicit val pickler: Pickler[MDSCoordinates] =
    generatePickler[MDSCoordinates]
}
case class MDSProportion(value: Array[Double])         extends Embedded
case class MDSCoordinates(value: Array[Array[Double]]) extends Embedded

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
