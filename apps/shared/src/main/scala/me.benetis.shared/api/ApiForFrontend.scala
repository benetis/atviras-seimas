package me.benetis.shared.api
import me.benetis.shared.{
  MdsPointWithAdditionalInfo,
  MdsResult,
  TermOfOfficeId
}

trait ApiForFrontend {
  def fetchMdsResults(termOfOfficeId: TermOfOfficeId)
    : Option[MdsResult[MdsPointWithAdditionalInfo]]
}
