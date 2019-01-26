package me.benetis.shared

import io.getquill.Embedded

case class PlenaryQuestionGroupId(plenary_question_group_id: String)
    extends Embedded
case class PlenaryQuestionTitle(title: String)                  extends Embedded
case class PlenaryQuestionTimeFrom(time_from: DateTimeOnlyTime) extends Embedded
case class PlenaryQuestionNumber(number: String)                extends Embedded
case class PlenaryQuestionDocumentLink(document_link: String)   extends Embedded
case class PlenaryQuestionSpeakers(speakers: Vector[String])    extends Embedded

sealed trait PlenaryQuestionStatus      extends Embedded
case object PlenaryQuestionAdoption     extends PlenaryQuestionStatus
case object PlenaryQuestionDiscussion   extends PlenaryQuestionStatus
case object PlenaryQuestionAffirmation  extends PlenaryQuestionStatus
case object PlenaryQuestionPresentation extends PlenaryQuestionStatus

case class PlenaryQuestion(agendaQuestionId: AgendaQuestionId,
                           plenaryQuestionGroupId: PlenaryQuestionGroupId,
                           title: PlenaryQuestionTitle,
                           timeFrom: PlenaryQuestionTimeFrom,
                           status: PlenaryQuestionStatus,
                           number: PlenaryQuestionNumber,
                           plenaryId: PlenaryId)
