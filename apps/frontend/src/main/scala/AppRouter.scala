import components.HomePage
import components.styleguide.StyleguidePage
import diode.react.ModelProxy
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.OnUnmount
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import me.benetis.shared.{
  MdsPointWithAdditionalInfo,
  MdsResult
}
import org.scalajs.dom.html.Div
import services.{AppCircuit, RootModel}

sealed trait Page
case object Home       extends Page
case object Styleguide extends Page

object AppRouter {

  val baseUrl = BaseUrl.fromWindowOrigin

  val config = RouterConfigDsl[Page].buildConfig {
    dsl: RouterConfigDsl[Page] =>
      import dsl._

      val circuit = AppCircuit.connect(_.mdsResult)

      (trimSlashes
        | staticRoute(root, Home) ~> renderR(
          ctl =>
            circuit(
              (p: ModelProxy[Option[
                MdsResult[MdsPointWithAdditionalInfo]
              ]]) => HomePage(HomePage.Props(p))
            )
        )
        | staticRoute("#styleguide", Styleguide) ~> renderR(
          ctl =>
            circuit(
              (_) => StyleguidePage()
            )
        ))
        .notFound(redirectToPage(Home)(Redirect.Replace))
        .renderWith(layout)
  }

  val router
    : Unmounted[Unit, Resolution[Page], OnUnmount.Backend] =
    Router(baseUrl, config)()

  def layout(
    c: RouterCtl[Page],
    r: Resolution[Page]
  ): VdomTagOf[Div] = {
    <.div(
      "Page",
      r.render()
    )
  }
}
