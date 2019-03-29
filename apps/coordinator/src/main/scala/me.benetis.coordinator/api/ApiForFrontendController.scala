package me.benetis.coordinator.api
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.{
  MDSRepo,
  ParliamentMemberRepo
}
import me.benetis.shared.{MdsResult, TermOfOfficeId}
import me.benetis.shared.api.ApiForFrontend

object ApiForFrontendController
    extends ApiForFrontend
    with LazyLogging {
  override def fetchMdsResults(
      termOfOfficeId: TermOfOfficeId): Option[MdsResult] = {
    MDSRepo.byTermOfOffice(termOfOfficeId)
  }
}
