package me.benetis.shared

import io.getquill.Embedded
import me.benetis.shared.dates.SharedDateTime

case class PlenaryId(plenary_id: Int)                     extends Embedded
case class PlenaryNumber(number: String)                  extends Embedded
case class PlenaryType(plenary_type: String)              extends Embedded
case class PlenaryTimeStart(time_start: SharedDateTime)   extends Embedded
case class PlenaryTimeFinish(time_finish: SharedDateTime) extends Embedded
case class Plenary(id: PlenaryId,
                   sessionId: SessionId,
                   number: PlenaryNumber,
                   plenaryType: PlenaryType,
                   timeStart: Option[PlenaryTimeStart],
                   timeFinish: Option[PlenaryTimeFinish])
    extends Embedded
