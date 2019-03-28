package me.benetis.shared

import boopickle.Default._
import io.getquill.Embedded

object Common {
  case class Point(x: Double, y: Double) extends Embedded

  object Point {
    implicit val pickler: Pickler[Point] =
      generatePickler[Point]
  }
}
