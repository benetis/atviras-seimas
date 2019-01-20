package me.benetis.downloader

import me.benetis.coordinator.downloader.{
  FactionDownloader,
  PlenaryDownloader,
  PlenaryQuestionDownloader,
  SessionDownloader,
  TermOfficeDownloader,
  VoteDownloader
}
import me.benetis.coordinator.repository.TermOfOfficeRepo
import org.scalatest._

class FetcherTest extends FreeSpec with Matchers {
  "test" - {
//    TermOfficeDownloader.fetchAndSave() should be(())
//    SessionDownloader.fetchAndSave() should be(())
    PlenaryQuestionDownloader.fetchAndSave() should be(())
//    PlenaryDownloader.fetchAndSave() should be(())
    VoteDownloader.fetchAndSave() should be(())
  }

//  "save" - {
//    TermOfOfficeRepo
//  }

}
