package me.benetis.shared

import io.getquill.Embedded

case class MultiFactionItemId(multi_faction_id: Int)
    extends Embedded
case class MultiFactionItemDistinctFactions(
  distinct_factions: Int)
    extends Embedded
case class MultiFactionItemFactionsList(
  factions: Vector[Faction])
    extends Embedded

case class MultiFactionItemFactionsListWithPersonId(
  personId: ParliamentMemberId,
  factionItemFactionsList: MultiFactionItemFactionsList)
    extends Embedded

case class MultiFactionItem(
  id: MultiFactionItemId,
  personName: ParliamentMemberName,
  personSurname: ParliamentMemberSurname,
  personId: ParliamentMemberId,
  distinctFactions: MultiFactionItemDistinctFactions,
  termOfOfficeId: TermOfOfficeId,
  factionsList: Option[MultiFactionItemFactionsList])
