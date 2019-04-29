package me.benetis.coordinator.computing

import org.joda.time.DateTime

package object MDS {
  type Matrix = Array[Array[Double]]

  case class EuclideanDistance(value: Double)
  case class TimeRangeOfMds(
    from: DateTime,
    to: DateTime)
}
