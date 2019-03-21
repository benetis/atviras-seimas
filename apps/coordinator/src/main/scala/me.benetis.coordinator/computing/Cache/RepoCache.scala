package me.benetis.coordinator.computing.Cache
import me.benetis.coordinator.repository.{
  ParliamentMemberRepo,
  TermOfOfficeRepo
}
import me.benetis.shared.{ParliamentMember, TermOfOffice}

object RepoCache {
  lazy val termOfOffices: List[TermOfOffice]       = TermOfOfficeRepo.list()
  lazy val parliameMembers: List[ParliamentMember] = ParliamentMemberRepo.list()
}
