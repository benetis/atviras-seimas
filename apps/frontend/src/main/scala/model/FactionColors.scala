package model

import me.benetis.shared.{
  FactionName,
  ParliamentMemberFactionName
}
import org.scalajs.dom

object FactionColors {
  case class FactionColor(value: String)

  val map: Map[String, FactionColor] = Map(
    "Lietuvos socialdemokratų partija" ->
      FactionColor("#ff0000"),
    "Lietuvos valstiečių ir žaliųjų sąjunga" ->
      FactionColor("#00ff00"),
    "Tėvynės sąjunga - Lietuvos krikščionys demokratai" ->
      FactionColor("#0000ff"),
    "Lietuvos Respublikos liberalų sąjūdis" ->
      FactionColor("#f2b51a"),
    "Partija Tvarka ir teisingumas" ->
      FactionColor("#e8f442"),
    "Darbo partija" ->
      FactionColor("#4591bc"),
    "Lietuvos lenkų rinkimų akcija-Krikščioniškų šeimų sąjunga" ->
      FactionColor("#a045bc")
  )

  def factionColor(
    factionName: ParliamentMemberFactionName
  ): FactionColor = {
    map.getOrElse(
      factionName.faction_name,
      FactionColor("#000000")
    )
  }
}
