package me.benetis.shared

import io.getquill.Embedded
import me.benetis.shared.dates.SharedDateOnly

case class TermOfOfficeId(term_of_office_id: Int)         extends Embedded
case class TermOfOfficeName(name: String)                 extends Embedded
case class TermOfOfficeDateFrom(dateFrom: SharedDateOnly) extends Embedded
case class TermOfOfficeDateTo(dateTo: SharedDateOnly)     extends Embedded
case class TermOfOffice(id: TermOfOfficeId,
                        name: TermOfOfficeName,
                        dateFrom: TermOfOfficeDateFrom,
                        dateTo: Option[TermOfOfficeDateTo])
