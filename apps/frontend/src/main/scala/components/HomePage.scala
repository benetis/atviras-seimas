package components

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import me.benetis.shared.{MdsResult, SessionId, TermOfOfficeId}
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry
import services.{LoadMdsResult, RootModel}

object HomePage {

  import styles.CssSettings._

  GlobalRegistry.register(new Style)
  val style = GlobalRegistry[Style].get

  case class Props(proxy: ModelProxy[Option[MdsResult]])

  private val component = ScalaComponent
    .builder[Props]("Home page")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props, Unit]) {
    def render(p: Props) = {
      <.div(
        <.div("inside render"),
        <.button(
          "MDS",
          ^.onClick --> {
            p.proxy.dispatchCB(LoadMdsResult(TermOfOfficeId(8)))
          }
        ),
        "Mds data:",
        p.proxy.value.fold(<.div("Empty MDS"))(
          result =>
            <.div(
              result.coordinates.value
                .map(row => row.mkString(" "))
                .mkString("\\n")
          )
        )
      )
    }
  }

  def apply(props: Props) = component(props)

  class Style extends StyleSheet.Inline {

    import dsl._

    val test = style(
      backgroundColor.red
    )

  }
}
