package me.benetis.downloader.Fetcher
import com.softwaremill.sttp._

object TermOfficeDownloader {

  def fetch() = {
    val request = sttp.get(uri"http://apps.lrs.lt/sip/p2b.ad_seimo_kadencijos")

    implicit val backend = HttpURLConnectionBackend()

    val response = request.send()

//    response match {
//      case Right(result) => TermOfOffice.
//    }

  }
}
