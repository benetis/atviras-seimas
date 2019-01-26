package me.benetis.shared

import io.getquill.Embedded

case class PersonId(person_id: Int)                extends Embedded
case class PersonFullName(person_fullname: String) extends Embedded
case class PersonName(person_name: String)         extends Embedded
case class PersonSurname(person_surname: String)   extends Embedded
