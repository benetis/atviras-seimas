package me.benetis.shared
import io.getquill.Embedded
import boopickle.Default._

case class EigenValues(value: Array[Double])
    extends Embedded

/* Data class used for visualization */
case class MdsPoint(
    x: Double,
    y: Double,
    id: ParliamentMemberTermOfOfficeSpecificId,
    factionName: ParliamentMemberFactionName,
    parliamentMemberId: ParliamentMemberId,
    parliamentMemberName: ParliamentMemberName,
    parliamentMemberSurname: ParliamentMemberSurname
) extends Embedded

object MdsPoint {
  implicit val pickler: Pickler[MdsPoint] =
    generatePickler[MdsPoint]
}

object EigenValues {
  import boopickle.Default._

  implicit val pickler: Pickler[EigenValues] =
    generatePickler[EigenValues]
}

object MDSProportion {
  implicit val pickler: Pickler[MDSProportion] =
    generatePickler[MDSProportion]
}

object MDSCoordinates {
  implicit val pickler: Pickler[MDSCoordinates] =
    generatePickler[MDSCoordinates]
}
case class MDSProportion(value: Array[Double])
    extends Embedded
case class MDSCoordinates(value: Vector[MdsPoint])
    extends Embedded

case class MdsResult(
    eigenValues: EigenValues,
    proportion: MDSProportion,
    coordinates: MDSCoordinates,
    createdAt: SharedDateTime,
    termOfOfficeId: TermOfOfficeId
) extends Embedded

object MdsResult {
  implicit val pickler: Pickler[MdsResult] =
    generatePickler[MdsResult]

}
