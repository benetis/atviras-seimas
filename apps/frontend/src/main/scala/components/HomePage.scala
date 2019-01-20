package components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry

object HomePage {

  import styles.CssSettings._

  GlobalRegistry.register(new Style)
  val style = GlobalRegistry[Style].get

  val component =
    ScalaComponent.builder
      .static("Home")(
        <.div("Home page. LolX2", style.test)
      )
      .build

  def apply() = component()

  class Style extends StyleSheet.Inline {

    import dsl._

    val test = style(
      backgroundColor.red
    )

  }
}
