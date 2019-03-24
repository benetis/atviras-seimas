package me.benetis.shared
import io.getquill.Embedded

case class EigenValues(value: Array[Double])
case class MDSProportion(value: Array[Double])
case class MDSCoordinates(value: Array[Array[Double]])

case class MdsResult(
    eigenValues: EigenValues,
    proportion: MDSProportion,
    coordinates: MDSCoordinates,
    termOfOfficeId: TermOfOfficeId
) extends Embedded
