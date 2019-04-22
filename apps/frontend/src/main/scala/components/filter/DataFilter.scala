package components.filter

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.SyntheticKeyboardEvent
import japgolly.scalajs.react.vdom.html_<^._
import model.FactionColors
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry
import services.{AddMdsFilter, GeneralStatisticsModel}

case class Filter(value: String)

object DataFilter {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Props(
    proxy: ModelProxy[GeneralStatisticsModel])

  case class State(text: String)

  private val component = ScalaComponent
    .builder[Props]("data filter")
    .initialState(State(""))
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props, State]) {

    def onKeyUp(
      e: ReactKeyboardEventFromInput
    )(
      p: Props,
      s: State
    ) = {
      val enter = 13
      if (e.keyCode == enter)
        addFilter(p, s)
      else
        Callback.empty
    }

    def addFilter(
      p: Props,
      s: State
    ) = {
      p.proxy.dispatchCB(AddMdsFilter(Filter(s.text)))
    }

    def onChange(e: ReactEventFromInput) = {
      val newValue = e.target.value
      $.modState(_.copy(text = newValue))
    }

    def render(
      p: Props,
      s: State
    ) = {
      <.div(
        styles.container,
        <.div(
          styles.inputBoxContainer,
          <.input(
            styles.inputBox,
            ^.placeholder := "Tekstas pagal kurį filtruoti",
            ^.onChange ==> onChange,
            ^.onKeyUp ==> { e =>
              onKeyUp(e)(p, s)
            },
            ^.key := "datafilter",
            ^.value := s.text
          ),
          <.div(
            styles.inputBoxButton,
            "+",
            ^.onClick --> addFilter(p, s)
          )
        ),
        <.div(
          styles.filtersContainer,
          p.proxy.value.mdsFilters.toVector
            .sortBy(_.value)
            .map(f => <.div(styles.appliedFilter, f.value))
            .toTagMod
        ),
        <.div()
      )
    }
  }

  def apply(props: Props) = component(props)

  class Style extends StyleSheet.Inline {

    import dsl._

    val container = style(
      display.flex,
      justifyContent.flexStart,
      alignItems.center,
      marginBottom(8 px),
      maxWidth(650 px),
      maxHeight(650 px),
      height(100 %%),
      width(100 %%),
      marginTop(35 px)
    )

    val inputBoxContainer = style(
      display.flex,
      flexDirection.row,
      boxShadow := "inset 0 0 8px #c7c7c7;",
      borderRadius(globalStyles.s.bitBorderRadius)
    )

    val inputBox = style(
      flexGrow(2),
      borderTopLeftRadius(globalStyles.s.bitBorderRadius),
      borderBottomLeftRadius(
        globalStyles.s.bitBorderRadius
      ),
      padding(8 px, 16 px),
      outline.none,
      border.none
    )

    val inputBoxButton = globalStyles.s.commonBottomStyles + style(
      padding(4 px, 8 px),
      borderTopRightRadius(globalStyles.s.bitBorderRadius),
      borderBottomRightRadius(
        globalStyles.s.bitBorderRadius
      ),
      display.flex,
      alignItems.center,
      fontFamily :=! "sans serif",
      fontWeight.normal,
      borderLeft :=! "1px solid white",
      &.hover(borderLeft :=! "1px solid lightgray"),
      userSelect := "none"
    )

    val appliedFilter = style(
      borderRadius(globalStyles.s.bitBorderRadius),
      fontWeight.bold,
      fontSize(0.8 em),
      padding(4 px, 8 px),
      margin(3 px),
      textAlign.center,
      backgroundColor(globalStyles.s.peach)
    )

    val filtersContainer = style(
      marginLeft(8 px),
      display.flex,
      flexWrap.wrap,
      maxWidth(100 %%),
      overflowX.auto
    )

  }
}
