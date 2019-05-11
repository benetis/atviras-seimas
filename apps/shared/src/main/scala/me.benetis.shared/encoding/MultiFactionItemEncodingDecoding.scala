package me.benetis.shared.encoding

import com.typesafe.scalalogging.LazyLogging
import me.benetis.shared.{
  Faction,
  FactionId,
  FactionName,
  MultiFactionItemFactionsList
}

object MultiFactionItemEncodingDecoding
    extends LazyLogging {
  def encode(list: MultiFactionItemFactionsList): String = {
    list.factions.map(_.id.faction_id).mkString("/")
  }

  def decode(
    value: String,
    factions: Vector[Faction]
  ): Option[MultiFactionItemFactionsList] = {
    if (value == null || value.isEmpty)
      None
    else
      Some(
        MultiFactionItemFactionsList(
          value
            .split('/')
            .toVector
            .flatMap(
              (split: String) => {
                factions
                  .find(
                    f =>
                      f.acronym.faction_acronym.toString == split
                  )
              }
            )
        )
      )
  }
}
