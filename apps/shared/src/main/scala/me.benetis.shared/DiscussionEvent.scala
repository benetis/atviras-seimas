package me.benetis.shared

import io.getquill.Embedded

sealed trait DiscussionEventType extends Embedded
case object Speech               extends DiscussionEventType
case object Registration         extends DiscussionEventType
case object Voting               extends DiscussionEventType

case class DiscussionEventTimeFrom(discussion_time_from: DateTimeOnlyTime)
    extends Embedded

case class DiscussionEvent(
    agendaQuestionId: AgendaQuestionId,
    timeFrom: DiscussionEventTimeFrom,
    eventType: DiscussionEventType,
    personId: Option[PersonId],
    personFullName: Option[PersonFullName],
    registrationId: Option[RegistrationId],
    voteId: Option[VoteId],
    voteType: Option[VoteType],
    plenaryId: PlenaryId
)
