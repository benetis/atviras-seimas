package me.benetis.shared

import io.getquill.Embedded

case class FactionId(faction_id: Int) extends Embedded
case class FactionName(faction_name: String)
    extends Embedded
case class FactionAcronym(faction_acronym: String)
    extends Embedded
case class Faction(id: FactionId,
                   name: FactionName,
                   acronym: FactionAcronym)
    extends Embedded
