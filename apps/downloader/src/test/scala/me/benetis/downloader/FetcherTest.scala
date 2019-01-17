package me.benetis.downloader

import me.benetis.downloader.Fetcher.{SessionDownloader, TermOfficeDownloader}
import me.benetis.shared.Repository.TermOfOfficeRepo
import org.scalatest._

class FetcherTest extends FreeSpec with Matchers {
  "test" - {
    SessionDownloader.fetchAndSave() should be(())
  }

//  "save" - {
//    TermOfOfficeRepo
//  }

}
