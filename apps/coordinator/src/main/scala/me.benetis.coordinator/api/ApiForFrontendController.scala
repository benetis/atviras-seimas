package me.benetis.coordinator.api
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.{
  KMeansRepo,
  MDSRepo,
  ParliamentMemberRepo
}
import me.benetis.coordinator.utils.{
  ComputingError,
  DBNotExpectedResult
}
import me.benetis.shared.{
  KMeansResult,
  MDSCoordinates,
  MdsPointOnlyXAndY,
  MdsPointWithAdditionalInfo,
  MdsResult,
  ParliamentMember,
  TermOfOfficeId
}
import me.benetis.shared.api.ApiForFrontend
import me.benetis.coordinator.computing.MDS.{
  AdditionalInfo,
  MultidimensionalScaling
}

object ApiForFrontendController
    extends ApiForFrontend
    with LazyLogging {
  override def fetchMdsList(
    termOfOfficeId: TermOfOfficeId
  ): Vector[MdsResult[MdsPointWithAdditionalInfo]] = {
    val mdsResultList =
      MDSRepo.byTermOfOffice(termOfOfficeId)

    val members = ParliamentMemberRepo.listByTermOfOffice(
      termOfOfficeId
    )

    mdsResultList.flatMap(
      (mds: MdsResult[MdsPointOnlyXAndY]) => {
        val updatedCoords = AdditionalInfo
          .transformToAdditionalInfo(mds, members)

        updatedCoords match {
          case Right(coords) =>
            Some(
              MdsResult[MdsPointWithAdditionalInfo](
                mds.id,
                mds.eigenValues,
                mds.proportion,
                coords,
                mds.createdAt,
                mds.termOfOfficeId,
                mds.rangeFrom,
                mds.rangeTo,
                mds.encoding
              )
            )
          case Left(err) =>
            logger.error(err.msg())
            None
        }
      }
    )
  }

  override def fetchKMeansResult(
    termOfOfficeId: TermOfOfficeId
  ): Option[KMeansResult] = {
    val result =
      KMeansRepo.byTermOfOffice(termOfOfficeId)

    result.headOption
  }

  override def fetchKMeansResults(
    termOfOfficeId: TermOfOfficeId
  ): Vector[KMeansResult] = {
    val result =
      KMeansRepo.byTermOfOffice(termOfOfficeId)

    result.toVector
  }

  override def fetchMdsResults(
    termOfOfficeId: TermOfOfficeId
  ): Option[MdsResult[MdsPointWithAdditionalInfo]] =
    fetchMdsList(termOfOfficeId).headOption

}
