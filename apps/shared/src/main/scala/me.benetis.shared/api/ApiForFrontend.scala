package me.benetis.shared.api
import me.benetis.shared.{MdsResult, TermOfOfficeId}

trait ApiForFrontend {
  def fetchMdsResults(termOfOfficeId: TermOfOfficeId): MdsResult
}
