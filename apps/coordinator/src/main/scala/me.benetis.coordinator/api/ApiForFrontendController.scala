package me.benetis.coordinator.api
import com.typesafe.scalalogging.LazyLogging
import me.benetis.coordinator.repository.{
  ClusteringRepo,
  MDSRepo,
  ParliamentMemberRepo
}
import me.benetis.coordinator.utils.{
  ComputingError,
  DBNotExpectedResult
}
import me.benetis.shared.{
  MDSCoordinates,
  MdsPointOnlyXAndY,
  MdsPointWithAdditionalInfo,
  MdsResult,
  ParliamentMember,
  TermOfOfficeId
}
import me.benetis.shared.api.ApiForFrontend
import cats.instances.vector._
import cats.syntax.traverse._
import cats.instances.either._

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
        val updatedCoords = mds.coordinates.value
          .map(p => addAdditionalInfoToMds(p, members))
          .sequence
          .map(MDSCoordinates(_))

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
                mds.rangeTo
              )
            )
          case Left(err) =>
            logger.error(err.msg())
            None
        }
      }
    )
  }

  override def fetchMdsResults(
    termOfOfficeId: TermOfOfficeId
  ): Option[MdsResult[MdsPointWithAdditionalInfo]] =
    fetchMdsList(termOfOfficeId).headOption

  private def addAdditionalInfoToMds(
    mdsPointOnly: MdsPointOnlyXAndY,
    termMembers: List[ParliamentMember]
  ): Either[ComputingError, MdsPointWithAdditionalInfo] = {

    val memberOpt = termMembers.find { m =>
      m.termOfOfficeSpecificId match {
        case Some(specId) => specId == mdsPointOnly.id
        case None         => false
      }
    }

    val memberEith = memberOpt match {
      case Some(m) => Right(m)
      case None =>
        Left(
          DBNotExpectedResult("specific id should be set")
        )
    }

    memberEith.map { m =>
      MdsPointWithAdditionalInfo(
        mdsPointOnly.x,
        mdsPointOnly.y,
        mdsPointOnly.id,
        m.factionName,
        m.personId,
        m.name,
        m.surname
      )
    }
  }
}
