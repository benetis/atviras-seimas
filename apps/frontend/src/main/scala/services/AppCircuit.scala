package services

import diode.ActionResult.ModelUpdate
import diode._
import autowire._
import diode.react.ReactConnector
import me.benetis.shared.DiscussionLength
import me.benetis.shared.api.ApiForFrontend
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

case class RootModel(counter: Int, discussionLength: Option[DiscussionLength])

case class Increase(amount: Int) extends Action
case class Decrease(amount: Int) extends Action
case object Reset                extends Action

case object LoadDiscussionLength extends Action
case class DiscussionLengthLoaded(discussionLength: DiscussionLength)
    extends Action

object AppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  def initialModel = RootModel(0, None)

  val counterHandler = new ActionHandler(zoomTo(_.counter)) {
    override def handle = {
      case Increase(a) => updated(value + a)
      case Decrease(a) => updated(value - a)
      case Reset       => updated(0)

    }
  }

  val vizHandler = new ActionHandler(zoomTo(_.discussionLength)) {
    override def handle = {
      case LoadDiscussionLength =>
        effectOnly(
          Effect(
            AjaxClient[ApiForFrontend]
              .getDiscussionLengths()
              .call()
              .map(DiscussionLengthLoaded)))
    }
  }

  val actionHandler = composeHandlers(counterHandler, vizHandler)
}
