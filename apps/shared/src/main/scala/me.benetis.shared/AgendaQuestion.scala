package me.benetis.shared

import io.getquill.Embedded

case class AgendaQuestionId(agenda_question_id: Int) extends Embedded
case class AgendaQuestionGroupId(agenda_question_group_id: String)
    extends Embedded
case class AgendaQuestionTitle(title: String)                extends Embedded
case class AgendaQuestionTimeFrom(time_from: SharedTimeOnly) extends Embedded
case class AgendaQuestionTimeTo(time_to: SharedTimeOnly)     extends Embedded
case class AgendaQuestionDateTimeFrom(datetime_from: SharedDateTime)
    extends Embedded
case class AgendaQuestionDateTimeTo(datetime_to: SharedDateTime)
    extends Embedded
case class AgendaQuestionNumber(number: String)              extends Embedded
case class AgendaQuestionDocumentLink(document_link: String) extends Embedded
case class AgendaQuestionSpeakers(speakers: Vector[String])  extends Embedded
case class AgendaQuestionStatusRaw(raw_status: String)       extends Embedded

sealed trait AgendaQuestionStatus             extends Embedded
case object Adoption                          extends AgendaQuestionStatus
case object Discussion                        extends AgendaQuestionStatus
case object Affirmation                       extends AgendaQuestionStatus
case object Presentation                      extends AgendaQuestionStatus
case object PresentationOfReturnedLawDocument extends AgendaQuestionStatus
case object Question                          extends AgendaQuestionStatus
case object InterpolationAnalysis             extends AgendaQuestionStatus
case object UnknownStatus                     extends AgendaQuestionStatus

case class AgendaQuestion(id: AgendaQuestionId,
                          groupId: AgendaQuestionGroupId,
                          title: AgendaQuestionTitle,
                          timeFrom: Option[AgendaQuestionTimeFrom],
                          timeTo: Option[AgendaQuestionTimeTo],
                          dateTimeFrom: Option[AgendaQuestionDateTimeFrom],
                          dateTimeTo: Option[AgendaQuestionDateTimeTo],
                          date: SharedTimeOnly,
                          statusRaw: AgendaQuestionStatusRaw,
                          status: Option[AgendaQuestionStatus],
                          documentLink: Option[AgendaQuestionDocumentLink],
                          speakers: AgendaQuestionSpeakers,
                          number: AgendaQuestionNumber,
                          plenaryId: PlenaryId)
