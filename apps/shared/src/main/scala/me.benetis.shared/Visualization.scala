package me.benetis.shared
import boopickle.Default._
sealed trait Visualization {}

object Visualization {
  implicit val todoPriorityPickler: Pickler[Visualization] =
    generatePickler[Visualization]
}

case class DiscussionLength(plenaryQuestion: PlenaryQuestion)
    extends Visualization
