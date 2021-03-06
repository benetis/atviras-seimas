package model

import me.benetis.shared.{
  FactionName,
  ParliamentMemberFactionName,
  ParliamentMemberId
}
import org.scalajs.dom

object FactionColors {
  case class FactionColor(value: String)

  val map: Map[String, FactionColor] = Map(
    "Lietuvos socialdemokratų partija" ->
      FactionColor("#e57575"),
    "Lietuvos valstiečių ir žaliųjų sąjunga" ->
      FactionColor("#85e575"),
    "Tėvynės sąjunga - Lietuvos krikščionys demokratai" ->
      FactionColor("#758de5"),
    "Lietuvos Respublikos liberalų sąjūdis" ->
      FactionColor("#e5894b"),
    "Partija Tvarka ir teisingumas" ->
      FactionColor("#e0e01f"),
    "Darbo partija" ->
      FactionColor("#4591bc"),
    "Lietuvos lenkų rinkimų akcija-Krikščioniškų šeimų sąjunga" ->
      FactionColor("#7e5491"),
    "Kitos frakcijos" -> FactionColor("#b5838d")
  )

  def factionColor(
    factionName: ParliamentMemberFactionName,
    parliamentMemberId: Option[ParliamentMemberId] = None
  ): FactionColor = {
    parliamentMemberId match {
      case Some(id) =>
        if (id == ParliamentMemberId(79168))
          FactionColor("#3ae8ff")
        else
          map.getOrElse(
            factionName.faction_name,
            FactionColor("#b5838d")
          )
      case None =>
        map.getOrElse(
          factionName.faction_name,
          FactionColor("#b5838d")
        )

    }
  }
}
