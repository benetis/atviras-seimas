package me.benetis.downloader

import me.benetis.downloader.Fetcher.{
  PlenaryDownloader,
  SessionDownloader,
  TermOfficeDownloader
}
import me.benetis.shared.Repository.TermOfOfficeRepo
import org.scalatest._

class FetcherTest extends FreeSpec with Matchers {
  "test" - {
//    TermOfficeDownloader.fetchAndSave() should be(())
//    SessionDownloader.fetchAndSave() should be(())
    PlenaryDownloader.fetchAndSave() should be(())
  }

//  "save" - {
//    TermOfOfficeRepo
//  }

}
