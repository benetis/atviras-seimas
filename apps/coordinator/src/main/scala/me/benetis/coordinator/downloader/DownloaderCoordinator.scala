package me.benetis.coordinator.downloader

import cats.effect.IO
import me.benetis.coordinator.repository.SessionRepo
import me.benetis.shared._

object DownloaderCoordinator {

  def apply(downloaderSettings: DownloaderSettings) = {
    downloaderSettings match {
      case FetchTermOfOffice =>
        TermOfficeDownloader.fetchAndSave()
      case FetchSessions =>
        SessionDownloader.fetchAndSave()
      case FetchPlenaries =>
        val sessions = SessionRepo.list()
        PlenaryDownloader.fetchAndSave(sessions)
      case FetchAgendaQuestions =>
        AgendaQuestionDownloader.fetchAndSave()
    }
  }

}
