package me.benetis.coordinator.downloader

import me.benetis.shared.{DownloaderSettings, FetchSessions, FetchTermOfOffice}

class DownloaderCoordinator {

  def apply(downloaderSettings: DownloaderSettings) = {
    downloaderSettings match {
      case FetchTermOfOffice =>
        TermOfficeDownloader.fetchAndSave()
      case FetchSessions =>
        PlenaryDownloader.fetchAndSave()
    }
  }

}
