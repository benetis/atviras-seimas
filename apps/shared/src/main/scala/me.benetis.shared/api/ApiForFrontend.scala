package me.benetis.shared.api
import me.benetis.shared.{
  KMeansResult,
  MdsPointWithAdditionalInfo,
  MdsResult,
  TermOfOfficeId
}

trait ApiForFrontend {
  def fetchMdsResults(
    termOfOfficeId: TermOfOfficeId
  ): Option[MdsResult[MdsPointWithAdditionalInfo]]

  def fetchMdsList(
    termOfOfficeId: TermOfOfficeId
  ): Vector[MdsResult[MdsPointWithAdditionalInfo]]

  def fetchKMeansResult(
    termOfOfficeId: TermOfOfficeId
  ): Option[KMeansResult]

  def fetchKMeansResults(
    termOfOfficeId: TermOfOfficeId
  ): Vector[KMeansResult]
}
