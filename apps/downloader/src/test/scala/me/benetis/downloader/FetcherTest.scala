package me.benetis.downloader

import me.benetis.downloader.Fetcher.{
  FactionDownloader,
  PlenaryDownloader,
  PlenaryQuestionDownloader,
  SessionDownloader,
  TermOfficeDownloader,
  VoteDownloader
}
import me.benetis.shared.Repository.TermOfOfficeRepo
import org.scalatest._

class FetcherTest extends FreeSpec with Matchers {
  "test" - {
//    TermOfficeDownloader.fetchAndSave() should be(())
//    SessionDownloader.fetchAndSave() should be(())
//    PlenaryQuestionDownloader.fetchAndSave() should be(())
    VoteDownloader.fetchAndSave() should be(())
  }

//  "save" - {
//    TermOfOfficeRepo
//  }

}
