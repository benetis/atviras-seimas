package me.benetis.shared.encoding

import com.typesafe.scalalogging.LazyLogging
import me.benetis.shared._

object Decoders extends LazyLogging {
  def agendaQuestionStatus(status: String): AgendaQuestionStatus = {
    status match {
      case "Tvirtinimas"                  => Affirmation
      case "Priėmimas"                    => Adoption
      case "Svarstymas"                   => Discussion
      case "Pateikimas"                   => Presentation
      case "Grąžinto įstatymo pateikimas" => PresentationOfReturnedLawDocument
      case "Klausimas"                    => Question
      case "Interpeliacijos nagrinėjimas" => InterpolationAnalysis
      case _ =>
        logger.error(s"Not supported status '$status'")
        UnknownStatus
    }
  }

}
