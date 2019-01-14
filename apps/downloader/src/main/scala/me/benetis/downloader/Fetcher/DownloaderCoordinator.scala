package me.benetis.downloader.Fetcher

import me.benetis.shared.{DownloaderSettings, FetchSessions, FetchTermOfOffice}

class DownloaderCoordinator {

  def apply(downloaderSettings: DownloaderSettings) = {
    downloaderSettings match {
      case FetchTermOfOffice => TermOfficeDownloader.fetch()
      case FetchSessions     => ???
    }
  }

}
