package components.charts

import me.benetis.shared.{
  ParliamentMemberName,
  ParliamentMemberSurname
}

object ChartUtils {
  def constructName(
    parliamentMemberName: ParliamentMemberName,
    parliamentMemberSurname: ParliamentMemberSurname
  ): String =
    s"${parliamentMemberName.person_name} ${parliamentMemberSurname.person_surname}"
}
