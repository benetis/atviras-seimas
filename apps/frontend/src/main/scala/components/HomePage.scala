package components

import components.generalStats.GeneralStatsPage
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import me.benetis.shared.{
  MdsPointWithAdditionalInfo,
  MdsResult,
  SessionId,
  TermOfOfficeId
}
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry
import services.{LoadMdsResult, RootModel}
import japgolly.scalajs.react.vdom.html_<^._
import utils.Pages.GeneralStats

object HomePage {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Props(
    proxy: ModelProxy[RootModel],
    ctl: RouterCtl[utils.Pages.Page])

  private val component = ScalaComponent
    .builder[Props]("Home page")
    .stateless
    .renderBackend[Backend]
    .componentDidMount(
      builder =>
        builder.backend.onComponentMount(builder.props)
    )
    .build

  class Backend($ : BackendScope[Props, Unit]) {

    def onComponentMount(p: Props): Callback = {
      p.proxy.dispatchCB(LoadMdsResult(TermOfOfficeId(8)))
    }

    def render(
      p: Props,
      s: Unit
    ) = {
      <.div(
        ^.height := "100%",
        globalStyles.s.pageContainer,
        <.div(
          styles.navigationContainer,
          p.ctl.link(GeneralStats)(
            globalStyles.s.navButton,
            "Kaip vienodai balsuoja seimo nariai?"
          )
        )
      )
    }
  }

  def apply(props: Props) = component(props)

  class Style extends StyleSheet.Inline {

    import dsl._

    val navigationContainer = style(
      height(100 %%),
      display.flex,
      justifyContent.center,
      alignItems.center
    )
  }
}
