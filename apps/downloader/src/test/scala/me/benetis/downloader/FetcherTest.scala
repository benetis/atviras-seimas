package me.benetis.downloader

import me.benetis.downloader.Fetcher.TermOfficeDownloader
import me.benetis.downloader.Saver.DataSaver
import org.scalatest._

class FetcherTest extends FreeSpec with Matchers {
  "test" - {
    TermOfficeDownloader.fetch() should be(())
  }

  "save" - {
    DataSaver()
  }

}
