package utils

object Pages {
  sealed trait Page
  case object Home           extends Page
  case object Styleguide     extends Page
  case object GeneralStatsML extends Page
  case object GeneralStats   extends Page

}
