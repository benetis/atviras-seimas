import components.HomePage
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.OnUnmount
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html.Div

sealed trait Page
case object Home extends Page

object AppRouter {

  val baseUrl = BaseUrl.fromWindowOrigin

  val config = RouterConfigDsl[Page].buildConfig { dsl =>
    import dsl._

    (trimSlashes
      | staticRoute(root, Home) ~> render(HomePage()))
      .notFound(redirectToPage(Home)(Redirect.Replace))
      .renderWith(layout)
  }

  val router: Unmounted[Unit, Resolution[Page], OnUnmount.Backend] =
    Router(baseUrl, config)()

  def layout(c: RouterCtl[Page], r: Resolution[Page]): VdomTagOf[Div] = {
    <.div(
      "Page",
      r.render()
    )
  }
}
