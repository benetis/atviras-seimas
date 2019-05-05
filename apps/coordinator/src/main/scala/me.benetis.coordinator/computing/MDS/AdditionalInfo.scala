package me.benetis.coordinator.computing.MDS

import me.benetis.coordinator.utils.{
  ComputingError,
  DBNotExpectedResult
}
import me.benetis.shared.{
  MDSCoordinates,
  MdsPointOnlyXAndY,
  MdsPointWithAdditionalInfo,
  MdsResult,
  ParliamentMember
}

import cats.instances.vector._
import cats.syntax.traverse._
import cats.instances.either._
object AdditionalInfo {

  def transformToAdditionalInfo(
    mds: MdsResult[MdsPointOnlyXAndY],
    members: List[ParliamentMember]
  ): Either[ComputingError, MDSCoordinates[
    MdsPointWithAdditionalInfo
  ]] = {
    mds.coordinates.value
      .map(
        p => addAdditionalInfoToMds(p, members)
      )
      .sequence
      .map(MDSCoordinates(_))
  }

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
