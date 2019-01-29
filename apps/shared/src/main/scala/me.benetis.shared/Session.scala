package me.benetis.shared

import io.getquill.Embedded
import me.benetis.shared.dates.SharedDateOnly

case class SessionId(session_id: Int)                 extends Embedded
case class SessionName(name: String)                  extends Embedded
case class SessionNumber(number: String)              extends Embedded
case class SessionDateFrom(date_from: SharedDateOnly) extends Embedded
case class SessionDateTo(date_to: SharedDateOnly)     extends Embedded
case class Session(id: SessionId,
                   termOfOfficeId: TermOfOfficeId,
                   number: SessionNumber,
                   name: SessionName,
                   date_from: SessionDateFrom,
                   date_to: Option[SessionDateTo])
