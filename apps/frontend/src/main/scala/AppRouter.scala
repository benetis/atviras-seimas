import components.HomePage
import components.generalStats.GeneralStatsPage
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

object AppRouter {
  import utils.Pages._

  val baseUrl = BaseUrl.fromWindowOrigin

  val config = RouterConfigDsl[Page].buildConfig {
    dsl: RouterConfigDsl[Page] =>
      import dsl._

      val circuit = AppCircuit.connect(r => r)

      (trimSlashes
        | staticRoute(root, Home) ~> renderR(
          ctl =>
            circuit(
              (p: ModelProxy[RootModel]) =>
                HomePage(
                  HomePage.Props(p, ctl)
                )
            )
        )
        | staticRoute("#general-stats", GeneralStats) ~> renderR(
          (ctl: RouterCtl[Page]) =>
            circuit(
              (p: ModelProxy[RootModel]) =>
                GeneralStatsPage(
                  GeneralStatsPage
                    .Props(p, ctl)
                )
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
      ^.height := "100%",
      r.render()
    )
  }
}
