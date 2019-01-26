package me.benetis.coordinator.downloader

import cats.effect.IO
import me.benetis.coordinator.repository.{PlenaryRepo, SessionRepo}
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
        val plenaries = PlenaryRepo.list()
//        val plenary = PlenaryRepo.findById(PlenaryId(-501307))
//        plenary.map(p => AgendaQuestionDownloader.fetchAndSave(List(p)))
        AgendaQuestionDownloader.fetchAndSave(plenaries)
      case FetchPlenaryQuestions =>
        DiscussionEventDownloader.fetchAndSave()
      case FetchDiscussionEvents =>
        DiscussionEventDownloader.fetchAndSave()
    }
  }

}
