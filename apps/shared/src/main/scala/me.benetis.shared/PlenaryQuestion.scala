package me.benetis.shared

import io.getquill.Embedded
import org.joda.time.DateTime

case class PlenaryQuestionGroupId(plenary_question_group_id: String)
    extends Embedded
case class PlenaryQuestionTitle(title: String)                  extends Embedded
case class PlenaryQuestionTimeFrom(time_from: DateTimeOnlyTime) extends Embedded
case class PlenaryQuestionDateTimeFrom(datetime_from: DateTime) extends Embedded
case class PlenaryQuestionNumber(number: String)                extends Embedded
case class PlenaryQuestionDocumentLink(document_link: String)   extends Embedded
case class PlenaryQuestionStatusRaw(plenary_raw_status: String) extends Embedded
case class PlenaryQuestionSpeakers(speakers: Vector[String])    extends Embedded

case class PlenaryQuestion(agendaQuestionId: AgendaQuestionId,
                           plenaryQuestionGroupId: PlenaryQuestionGroupId,
                           title: PlenaryQuestionTitle,
                           timeFrom: PlenaryQuestionTimeFrom,
                           dateTimeFrom: PlenaryQuestionDateTimeFrom,
                           status: Option[AgendaQuestionStatus],
                           statusRaw: Option[PlenaryQuestionStatusRaw],
                           number: PlenaryQuestionNumber,
                           plenaryId: PlenaryId)
