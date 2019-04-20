package components.filter

import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.SyntheticKeyboardEvent
import japgolly.scalajs.react.vdom.html_<^._
import model.FactionColors
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry

object DataFilter {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Filter(value: String)

  case class Props()

  case class State(
    text: String,
    filters: Set[Filter])

  private val component = ScalaComponent
    .builder[Props]("data filter")
    .initialState(State("", Set.empty))
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props, State]) {

    def addFilter(filter: Filter): Callback =
      $.modState(
        s =>
          s.copy(
            filters = s.filters ++ Set(filter),
            text = ""
          )
      )

    def onKeyUp(
      e: ReactKeyboardEventFromInput
    )(
      state: State
    ) = {
      val enter = 13
      if (e.keyCode == enter)
        addFilter(Filter(state.text))
      else
        Callback.empty
    }

    def onChange(e: ReactEventFromInput) = {
      dom.console.log($.state.runNow().filters.toString())
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
            ^.placeholder := "Žodis pagal kurį filtruoti",
            ^.onChange ==> onChange,
            ^.onKeyUp ==> { e =>
              onKeyUp(e)(s)
            },
            ^.key := "datafilter",
            ^.value := s.text
          ),
          <.div(styles.inputBoxButton, "+")
        ),
        <.div(
          styles.filtersContainer,
          s.filters.toVector
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
      fontWeight.normal
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
