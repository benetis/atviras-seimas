package me.benetis.shared

import io.getquill.Embedded

sealed trait DiscussionEventType extends Embedded
case object Speech               extends DiscussionEventType
case object Registration         extends DiscussionEventType
case object Voting               extends DiscussionEventType

case class DiscussionEventUniqueId(unique_id: String) extends Embedded

case class DiscussionEventTimeFrom(discussion_time_from: SharedTimeOnly)
    extends Embedded

case class DiscussionEvent(
    agendaQuestionId: AgendaQuestionId,
    uniqueId: DiscussionEventUniqueId,
    timeFrom: Option[DiscussionEventTimeFrom],
    eventType: Option[DiscussionEventType],
    personId: Option[ParliamentMemberId],
    personFullName: Option[ParliamentMemberFullName],
    registrationId: Option[RegistrationId],
    voteId: Option[VoteId],
    voteType: Option[VoteType],
    plenaryId: PlenaryId,
)
