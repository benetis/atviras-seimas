package me.benetis.shared

import io.getquill.Embedded
import org.joda.time.DateTime

case class PlenaryId(plenary_id: Int)               extends Embedded
case class PlenaryNumber(number: String)            extends Embedded
case class PlenaryType(plenary_type: String)        extends Embedded
case class PlenaryTimeStart(time_start: DateTime)   extends Embedded
case class PlenaryTimeFinish(time_finish: DateTime) extends Embedded
case class Plenary(id: PlenaryId,
                   sessionId: SessionId,
                   number: PlenaryNumber,
                   plenaryType: PlenaryType,
                   timeStart: Option[PlenaryTimeStart],
                   timeFinish: Option[PlenaryTimeFinish])
    extends Embedded
