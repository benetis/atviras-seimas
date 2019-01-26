package me.benetis.shared

import io.getquill.Embedded
import org.joda.time.DateTime

sealed trait DownloaderSettings

//Dar pagalvoti ar daryt tik naujiems duomenims, gal pradzioje nedaryt
case object FetchTermOfOffice    extends DownloaderSettings
case object FetchSessions        extends DownloaderSettings
case object FetchPlenaries       extends DownloaderSettings
case object FetchAgendaQuestions extends DownloaderSettings
