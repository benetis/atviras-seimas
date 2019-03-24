package me.benetis.shared
import io.getquill.Embedded

case class MDSResults(
    eigenValues: Array[Double],
    proportion: Array[Double],
    coordinates: Array[Array[Double]],
    termOfOfficeId: TermOfOfficeId
)
