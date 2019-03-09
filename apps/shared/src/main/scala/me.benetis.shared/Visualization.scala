package me.benetis.shared
sealed trait Visualization {}

object Visualization {

}

case class DiscussionLength(plenaryQuestion: PlenaryQuestion)
    extends Visualization
