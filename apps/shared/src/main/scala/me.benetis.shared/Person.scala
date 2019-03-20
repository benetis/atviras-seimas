package me.benetis.shared

import io.getquill.Embedded

case class ParliamentMemberId(person_id: Int)                extends Embedded
case class ParliamentMemberFullName(person_fullname: String) extends Embedded
case class ParliamentMemberName(person_name: String)         extends Embedded
case class ParliamentMemberSurname(person_surname: String)   extends Embedded

case class ParliamentMemberUniqueId(unique_id: String)         extends Embedded
case class ParliamentMemberGender(gender: String)              extends Embedded
case class ParliamentMemberDateFrom(date_from: SharedDateOnly) extends Embedded
case class ParliamentMemberDateTo(date_to: SharedDateOnly)     extends Embedded
case class ParliamentMemberFactionName(faction_name: String)   extends Embedded
case class ParliamentMemberElectedHow(elected_how: String)     extends Embedded
case class ParliamentMemberTermOfOfficeAmount(term_of_office_amount: Int)
    extends Embedded
case class ParliamentMemberBiographyLink(biography_link: String)
    extends Embedded
case class ParliamentMember(
    uniqueId: ParliamentMemberUniqueId,
    personId: ParliamentMemberId,
    name: ParliamentMemberName,
    surname: ParliamentMemberSurname,
    gender: ParliamentMemberGender,
    date_from: ParliamentMemberDateFrom,
    date_to: Option[ParliamentMemberDateTo],
    factionName: ParliamentMemberFactionName,
    electedHow: ParliamentMemberElectedHow,
    termOfOfficeAmount: ParliamentMemberTermOfOfficeAmount,
    biographyLink: ParliamentMemberBiographyLink
) extends Embedded
