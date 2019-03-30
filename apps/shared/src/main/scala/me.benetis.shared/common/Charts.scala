package me.benetis.shared.common

object Charts {
  trait ScatterPoint {
    val x: Double
    val y: Double
  }

  case class ScatterPlotPointPosition(
    x: Double,
    y: Double)

}
