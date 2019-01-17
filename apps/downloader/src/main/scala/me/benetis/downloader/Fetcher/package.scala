package me.benetis.downloader

import org.joda.time.DateTime
import scala.xml.Node

package object Fetcher {
  implicit class nodeExt(val o: Node) extends AnyVal {
    def tagText(tag: String): String = (o \ s"@$tag").text
  }
}

sealed trait FetcherError

case object BadDateFormat extends FetcherError
