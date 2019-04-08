package components.generalStats

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import me.benetis.shared.{
  MdsPointWithAdditionalInfo,
  MdsResult,
  TermOfOfficeId
}
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry
import services.LoadMdsResult
import utils.Pages.{GeneralStats, Home}

object GeneralStatsPage {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Props(
    proxy: ModelProxy[
      Option[MdsResult[MdsPointWithAdditionalInfo]]
    ],
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
        globalStyles.s.pageContainer,
        styles.statsContainer,
        <.div(
          styles.statsNavigation,
          p.ctl.link(Home)(
            globalStyles.s.navButton,
            "← Pradinis puslapis"
          ),
          <.div(
            styles.activeTabContainer,
            <.button(styles.tab, "MDS"),
            <.button(styles.tab, "Klasterizacija")
          )
        ),
        <.p("Mds data:"),
        MDSChart.apply(MDSChart.Props(p.proxy.value))
      )
    }
  }

  def apply(props: Props) = component(props)

  class Style extends StyleSheet.Inline {

    import dsl._

    val statsNavigation = style(
      marginTop(35 px),
      marginBottom(5 px),
      display.flex,
      flexWrap.wrap,
      flexDirection.column,
      alignItems.center
    )

    val statsContainer = style(
      display.flex,
      flexDirection.column,
      alignItems.center
    )

    val activeTabContainer = style(
      marginTop(5 px),
      marginBottom(5 px)
    )

    val tab = style(
      textDecoration := "none",
      backgroundColor(globalStyles.s.parrotPink),
      color.white,
      display.inlineBlock,
      textAlign.center,
      padding(10 px, 15 px),
      borderRadius(5 px),
      fontWeight.bold,
      border.none,
      &.hover - (
        color(globalStyles.s.parrotPink),
        backgroundColor(white)
      ),
      marginLeft(5 px)
    )

  }
}
