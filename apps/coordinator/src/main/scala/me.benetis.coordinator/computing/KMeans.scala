package me.benetis.coordinator.computing

import me.benetis.coordinator.utils.ComputingError
import me.benetis.shared.TermOfOfficeId
import smile.clustering._

case class KMeansResult()

object KMeansComputing {
  def compute(
    termOfOfficeId: TermOfOfficeId
  ): Either[ComputingError, KMeansResult] = {
    ???
    /**
    * One point - one parliament's votes
      parameters - how many votes we are looking at

    NameX - 1, 0, 2, 0, ...
    NameY - 1, 1, 2, -1, ...
    */
  }
}
