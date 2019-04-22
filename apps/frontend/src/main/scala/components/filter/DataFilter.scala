package components.filter

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.SyntheticKeyboardEvent
import japgolly.scalajs.react.vdom.html_<^._
import model.FactionColors
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.HTMLInputElement
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry
import services.{
  AddMdsFilter,
  GeneralStatisticsModel,
  RemoveMdsFilter
}

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

    private val inputRef = Ref[html.Input]

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
    ): Callback = {
      p.proxy
        .dispatchCB(AddMdsFilter(Filter(s.text)))
        .flatMap(_ => $.modState(_.copy(text = "")))
    }

    def removeFilter(
      filter: Filter,
      p: Props
    ) =
      p.proxy
        .dispatchCB(RemoveMdsFilter(filter))
        .flatMap(_ => inputRef.foreach(_.focus()))

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
            ^.autoFocus := true,
            styles.inputBox,
            ^.placeholder := "Tekstas pagal kurį filtruoti",
            ^.onChange ==> onChange,
            ^.onKeyUp ==> { e =>
              onKeyUp(e)(p, s)
            },
            ^.key := "datafilter",
            ^.value := s.text
          ).withRef(inputRef),
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
            .map(
              f =>
                <.div(
                  ^.onClick --> removeFilter(f, p),
                  styles.appliedFilter,
                  <.span(styles.appliedFilterText, f.value),
                  <.div(
                    styles.appliedFilterRemove,
                    "\u2715"
                  )
                )
            )
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
      display.flex,
      flexWrap.nowrap,
      justifyContent.spaceBetween,
      alignItems.center,
      borderRadius(globalStyles.s.bitBorderRadius),
      fontWeight.bold,
      fontSize(0.8 em),
      paddingTop(4 px),
      paddingBottom(4 px),
      paddingLeft(8 px),
      paddingRight(4 px),
      margin(3 px),
      textAlign.center,
      backgroundColor(globalStyles.s.peach),
      cursor.pointer
    )

    val appliedFilterText = style(
      lineHeight(0.8 em)
    )

    val appliedFilterRemove = style(
      borderRadius(50 %%),
      backgroundColor(globalStyles.s.oldLavender),
      padding(2 px, 1.5 px, 0.5 px, 1 px),
      marginLeft(3 px),
      fontSize(0.8 em),
      lineHeight(0.8 em)
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
