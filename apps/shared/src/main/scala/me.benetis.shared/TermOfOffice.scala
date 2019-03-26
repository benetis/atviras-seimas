package me.benetis.shared
import boopickle.Default._

import io.getquill.Embedded

case class TermOfOfficeId(term_of_office_id: Int) extends Embedded

object TermOfOfficeId {
  implicit val pickler: Pickler[TermOfOfficeId] =
    generatePickler[TermOfOfficeId]
}

case class TermOfOfficeName(name: String)                 extends Embedded
case class TermOfOfficeDateFrom(dateFrom: SharedDateOnly) extends Embedded
case class TermOfOfficeDateTo(dateTo: SharedDateOnly)     extends Embedded
case class TermOfOffice(id: TermOfOfficeId,
                        name: TermOfOfficeName,
                        dateFrom: TermOfOfficeDateFrom,
                        dateTo: Option[TermOfOfficeDateTo])
