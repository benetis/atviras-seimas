package me.benetis.coordinator.api
import me.benetis.coordinator.repository.MDSRepo
import me.benetis.shared.{MdsResult, TermOfOfficeId}
import me.benetis.shared.api.ApiForFrontend

object ApiForFrontendController extends ApiForFrontend {
  override def fetchMdsResults(
      termOfOfficeId: TermOfOfficeId): Option[MdsResult] =
    MDSRepo.byTermOfOffice(termOfOfficeId)
}
