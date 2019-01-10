package me.benetis.shared

import org.joda.time.DateTime

sealed trait DownloaderSettings


/**
  * <EigosKlausimas laikas_nuo="15:07" numeris="2 - 7." svarstomo_klausimo_id="-31496"
  * stadija="Priėmimas" pavadinimas="Su nekilnojamuoju turtu susijusio kredito įstatymo Nr. XII-2769 12 ir 16 straipsnių pakeitimo įstatymo projektas (Nr. XIIIP-1712(2))"/>
  */

case class PlenaryQuestionId(value: String)
case class PlenaryQuestionTitle(value: String)
case class PlenaryQuestionTimeFrom(value: DateTime)
case class PlenaryQuestionNumber(value: String)

sealed trait PlenaryQuestionStatus
case object Admission extends PlenaryQuestionStatus
case object Discussion extends PlenaryQuestionStatus
case object Approval extends PlenaryQuestionStatus

case class PlenaryQuestion(id: PlenaryQuestionId,
                           title: PlenaryQuestionTitle,
                           timeFrom: PlenaryQuestionTimeFrom,
                           number: PlenaryQuestionNumber,
                           status: PlenaryQuestionStatus
                          )

case class PlenaryId(value: String)
case class Plenary(id: PlenaryId, sessionId: SessionId)

case class SessionId(value: String)
case class SessionName(value: String)
case class SessionNumber(value: String)
case class SessionTimeFrom(value: DateTime)
case class SessionTimeTo(value: DateTime)
case class Session(id: SessionId,
                   termOfOfficeId: TermOfOfficeId,
                   number: SessionNumber,
                   name: SessionName,
                   from: SessionTimeFrom,
                   to: SessionTimeTo)

case class TermOfOfficeId(value: String)
case class TermOfOffice(id: TermOfOfficeId)