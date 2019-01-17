package me.benetis.downloader

import me.benetis.downloader.Fetcher.{PlenaryDownloader, TermOfficeDownloader}
import me.benetis.shared.Repository.TermOfOfficeRepo
import org.scalatest._

class FetcherTest extends FreeSpec with Matchers {
  "test" - {
    PlenaryDownloader.fetchAndSave() should be(())
  }

//  "save" - {
//    TermOfOfficeRepo
//  }

}
