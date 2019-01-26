package me.benetis.coordinator

import me.benetis.coordinator.downloader.{
  DownloaderCoordinator,
  FactionDownloader,
  PlenaryDownloader,
  AgendaQuestionAgendaDownloader,
  SessionDownloader,
  TermOfficeDownloader,
  VoteDownloader
}
import me.benetis.coordinator.repository.TermOfOfficeRepo
import me.benetis.shared.{FetchPlenaries, FetchAgendaQuestions}
import org.scalatest._

class FetcherTest extends FreeSpec with Matchers {
  "test" - {
//    TermOfficeDownloader.fetchAndSave() should be(())
//    SessionDownloader.fetchAndSave() should be(())
//    AgendaQuestionAgendaDownloader.fetchAndSave() should be(())
//    PlenaryDownloader.fetchAndSave() should be(())
//    VoteDownloader.fetchAndSave() should be(())
    DownloaderCoordinator(FetchAgendaQuestions)

  }

//  "save" - {
//    TermOfOfficeRepo
//  }

}
