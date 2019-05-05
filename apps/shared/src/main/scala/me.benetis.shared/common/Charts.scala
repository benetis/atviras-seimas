package me.benetis.shared.common

import me.benetis.shared._

object Charts {
  trait ScatterPoint {
    val x: Double
    val y: Double
  }

  trait PointWithParliamentInfo {
    val factionName: ParliamentMemberFactionName
    val parliamentMemberId: ParliamentMemberId
    val parliamentMemberName: ParliamentMemberName
    val parliamentMemberSurname: ParliamentMemberSurname
  }

  case class ScatterPlotPointPosition(
    x: Double,
    y: Double)

}
