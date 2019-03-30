package components

import diode.react.ModelProxy
import japgolly.scalajs.react._
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
import scala.scalajs.js
import js.JSConverters._
import model.FactionColors
import org.scalajs.dom

object FactionLegend {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Props()

  private val component = ScalaComponent
    .builder[Props]("Faction legend")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props, Unit]) {

    def render(
      p: Props,
      s: Unit
    ) = {

      <.ul(
        FactionColors.map.keys
          .map(
            k =>
              <.li(
                styles.legendItem,
                k,
                <.span(
                  ^.backgroundColor := s"${FactionColors.map(k).value}",
                  styles.legendColor()
                )
              )
          )
          .toTagMod
      )
    }
  }

  def apply(props: Props) = component(props)

  class Style extends StyleSheet.Inline {

    import dsl._

    val legendItem = style(
      display.flex,
      alignItems.center,
      height(25 px)
    )

    val legendColor = style(
      marginLeft(5 px),
      width(15 px),
      height(15 px),
      borderRadius(50 %%)
    )

  }
}
