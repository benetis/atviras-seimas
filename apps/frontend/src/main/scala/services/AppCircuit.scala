package services

import diode.ActionResult.ModelUpdate
import diode._
import diode.react.ReactConnector
import me.benetis.shared.api.ApiForFrontend
import me.benetis.shared.{DiscussionLength, MdsResult, TermOfOfficeId}
import scala.concurrent.ExecutionContext.Implicits.global
import autowire._

case class RootModel(counter: Int, mdsResult: Option[MdsResult])

case class Increase(amount: Int) extends Action
case class Decrease(amount: Int) extends Action
case object Reset                extends Action

case class LoadMdsResult(termOfOfficeId: TermOfOfficeId) extends Action
case class MdsResultLoaded(mdsResult: MdsResult)         extends Action

object AppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  def initialModel = RootModel(0, None)

  val counterHandler = new ActionHandler(zoomTo(_.counter)) {
    override def handle = {
      case Increase(a) => updated(value + a)
      case Decrease(a) => updated(value - a)
      case Reset       => updated(0)

    }
  }

  val vizHandler = new ActionHandler(zoomTo(_.mdsResult)) {
    override def handle = {
      case LoadMdsResult(termOfOfficeId) =>
        effectOnly(
          Effect(
            AjaxClient[ApiForFrontend]
              .fetchMdsResults(termOfOfficeId)
              .call()
              .map(MdsResultLoaded)
          )
        )
      case MdsResultLoaded(mdsResult) =>
        updated(Some(mdsResult))
    }
  }

  val actionHandler = composeHandlers(counterHandler, vizHandler)
}
