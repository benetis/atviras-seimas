package me.benetis.shared

import io.getquill.Embedded

case class AgendaQuestionId(agenda_question_id: Int) extends Embedded
case class AgendaQuestionGroupId(agenda_question_group_id: String)
    extends Embedded
case class AgendaQuestionTitle(title: String)                  extends Embedded
case class AgendaQuestionTimeFrom(time_from: DateTimeOnlyTime) extends Embedded
case class AgendaQuestionTimeTo(time_to: DateTimeOnlyTime)     extends Embedded
case class AgendaQuestionNumber(number: String)                extends Embedded
case class AgendaQuestionDocumentLink(document_link: String)   extends Embedded
case class AgendaQuestionSpeakers(speakers: Vector[String])    extends Embedded

sealed trait AgendaQuestionStatus extends Embedded
case object Adoption              extends AgendaQuestionStatus
case object Discussion            extends AgendaQuestionStatus
case object Affirmation           extends AgendaQuestionStatus
case object Presentation          extends AgendaQuestionStatus

case class AgendaQuestion(id: AgendaQuestionId,
                          groupId: AgendaQuestionGroupId,
                          title: AgendaQuestionTitle,
                          timeFrom: Option[AgendaQuestionTimeFrom],
                          timeTo: Option[AgendaQuestionTimeTo],
                          status: AgendaQuestionStatus,
                          documentLink: AgendaQuestionDocumentLink,
                          speakers: AgendaQuestionSpeakers,
                          number: AgendaQuestionNumber)
