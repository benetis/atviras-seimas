package me.benetis.coordinator.downloader

import cats.effect.IO
import me.benetis.coordinator.repository._
import me.benetis.shared._

object Coordinator {

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
        AgendaQuestionDownloader.fetchAndSave(plenaries)
      case FetchPlenaryQuestions =>
        val plenaries = PlenaryRepo.list()
        PlenaryQuestionDownloader.fetchAndSave(plenaries)
      case FetchDiscussionEvents =>
        val agendaQuestions =
          AgendaQuestionRepo.list()
        DiscussionEventDownloader.fetchAndSave(
          agendaQuestions)
      case FetchVotes =>
        val voteIdsToDownload =
          DiscussionEventRepo.distinctVoteIds()
        VoteDownloader.fetchAndSave(voteIdsToDownload)
      case FetchFactions =>
        FactionDownloader.fetchAndSave()
      case FetchParliamentMembers =>
        val terms = TermOfOfficeRepo.list()
        ParliamentMembersDownloader.fetchAndSave(terms)
    }
  }

}
