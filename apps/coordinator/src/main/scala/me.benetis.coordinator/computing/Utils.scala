package me.benetis.coordinator.computing

import me.benetis.coordinator.repository.{
  MultiFactionItemRepo,
  ParliamentMemberRepo
}
import me.benetis.shared.{ParliamentMember, TermOfOfficeId}

object Utils {

  def membersForSingleFaction(
    termOfOfficeId: TermOfOfficeId
  ): List[ParliamentMember] = {
    val intermediary =
      ParliamentMemberRepo.listByTermOfOffice(
        termOfOfficeId
      )

    val multiFactionsItems =
      MultiFactionItemRepo.byTermOfOffice(
        termOfOfficeId
      )

    intermediary.filter(
      p =>
        !multiFactionsItems
          .exists(_.personId == p.personId)
    )
  }

}
