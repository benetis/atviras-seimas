package components.generalStatsML

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.Reusable
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import me.benetis.shared.{
  KMeansId,
  MdsPointWithAdditionalInfo,
  MdsResult,
  MdsResultId,
  TermOfOfficeId
}
import diode.ActionHandler._
import org.scalajs.dom
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry
import services.{
  GeneralStatsSelectedChart,
  LoadKMeansResult,
  LoadMdsResult,
  RootModel,
  SelectedClustering,
  SelectedMdsChart,
  SetSelectedGeneralStatsTab,
  SetSelectedKMeansResult,
  SetSelectedMdsResult
}
import utils.Pages.{GeneralStatsML, Home}

object GeneralStatsMLPage {

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
      p.proxy
        .dispatchCB(LoadMdsResult(TermOfOfficeId(8))) >> p.proxy
        .dispatchCB(LoadKMeansResult(TermOfOfficeId(8)))
    }

    def tabButton(text: String)(isActive: Boolean) =
      <.button(
        styles.tab,
        text,
        if (isActive) styles.activeTab
        else <.span()
      )

    def tabButtons(p: Props): TagMod = {

      val selectedChart =
        p.proxy.value.generalStats.selectedGeneralStatsChart

      val isMds        = selectedChart == SelectedMdsChart
      val isClustering = selectedChart == SelectedClustering

      <.div(
        <.button(
          styles.tab,
          ^.onClick --> p.proxy
            .dispatchCB(
              SetSelectedGeneralStatsTab(SelectedMdsChart)
            ),
          "MDS",
          if (isMds) styles.activeTab
          else <.span()
        ),
        <.button(
          styles.tab,
          ^.onClick --> p.proxy
            .dispatchCB(
              SetSelectedGeneralStatsTab(SelectedClustering)
            ),
          "Klasterizacija",
          if (isClustering) styles.activeTab
          else <.span()
        )
      )
    }

    val onDateRangeChange =
      Reusable.fn((id: MdsResultId) => {

        $.props.flatMap(
          props =>
            props.proxy.dispatchCB(SetSelectedMdsResult(id))
        )

      })

    val onKMeansResultChange =
      Reusable.fn((id: KMeansId) => {

        $.props.flatMap(
          props =>
            props.proxy.dispatchCB(
              SetSelectedKMeansResult(id)
            )
        )

      })

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
            tabButtons(p)
          )
        ),
        p.proxy.value.generalStats.selectedGeneralStatsChart match {
          case SelectedMdsChart =>
            <.div(
              MDSChart.apply(
                MDSChart.Props(
                  p.proxy.value.generalStats.mdsResults,
                  p.proxy.value.generalStats.mdsSelectedId,
                  onDateRangeChange,
                  p.proxy.zoom(_.generalStats)
                )
              )
            )
          case SelectedClustering =>
            <.div(
              KMeansChart.apply(
                KMeansChart.Props(
                  p.proxy.value.generalStats.kMeansResults,
                  p.proxy.value.generalStats.kMeansSelectedId,
                  onKMeansResultChange,
                  p.proxy.zoom(_.generalStats)
                )
              )
            )
        }
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
      alignItems.center,
      minHeight(100 vh),
      height(100 %%)
    )

    val activeTabContainer = style(
      marginTop(10 px),
      marginBottom(10 px)
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

    val activeTab = style(
      &.hover - (transform := "scale(1.05)"),
      color(globalStyles.s.parrotPink),
      backgroundColor(white)
    )

  }
}
