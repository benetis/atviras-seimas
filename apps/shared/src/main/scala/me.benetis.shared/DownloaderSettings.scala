package me.benetis.shared

sealed trait DownloaderSettings

case object FetchTermOfOffice      extends DownloaderSettings
case object FetchSessions          extends DownloaderSettings
case object FetchPlenaries         extends DownloaderSettings
case object FetchAgendaQuestions   extends DownloaderSettings
case object FetchPlenaryQuestions  extends DownloaderSettings
case object FetchDiscussionEvents  extends DownloaderSettings
case object FetchVotes             extends DownloaderSettings
case object FetchParliamentMembers extends DownloaderSettings
