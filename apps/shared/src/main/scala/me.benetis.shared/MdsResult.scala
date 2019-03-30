package me.benetis.shared
import io.getquill.Embedded
import boopickle.Default._
import me.benetis.shared.common.Charts.ScatterPoint

case class EigenValues(value: Array[Double])
    extends Embedded

case class MdsPointWithAdditionalInfo(
    x: Double,
    y: Double,
    id: ParliamentMemberTermOfOfficeSpecificId,
    factionName: ParliamentMemberFactionName,
    parliamentMemberId: ParliamentMemberId,
    parliamentMemberName: ParliamentMemberName,
    parliamentMemberSurname: ParliamentMemberSurname
) extends Embedded
    with ScatterPoint

/* Data class used for visualization */
case class MdsPointOnlyXAndY(
    x: Double,
    y: Double,
    id: ParliamentMemberTermOfOfficeSpecificId,
) extends Embedded
    with ScatterPoint

object MdsPointOnlyXAndY {
  implicit val pickler: Pickler[MdsPointOnlyXAndY] =
    generatePickler[MdsPointOnlyXAndY]
}

object MdsPointWithAdditionalInfo {
  implicit val pickler
    : Pickler[MdsPointWithAdditionalInfo] =
    generatePickler[MdsPointWithAdditionalInfo]
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
  implicit val pickler
    : Pickler[MDSCoordinates[MdsPointWithAdditionalInfo]] =
    generatePickler[
      MDSCoordinates[MdsPointWithAdditionalInfo]]
}
case class MDSProportion(value: Array[Double])
    extends Embedded
case class MDSCoordinates[T <: ScatterPoint](
    value: Vector[T])
    extends Embedded

case class MdsResult[T <: ScatterPoint](
    eigenValues: EigenValues,
    proportion: MDSProportion,
    coordinates: MDSCoordinates[T],
    createdAt: SharedDateTime,
    termOfOfficeId: TermOfOfficeId
) extends Embedded

object MdsResult {
  implicit val pickler: Pickler[MdsResult[ScatterPoint]] =
    compositePickler[MdsResult[ScatterPoint]]

}
