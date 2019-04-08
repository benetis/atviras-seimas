import scalacss.defaults.Exports
import scalacss.internal.mutable.Settings
import scalacss.internal.mutable.GlobalRegistry

package object globalStyles {
  val CssSettings: Exports with Settings =
    scalacss.devOrProdDefaults

  import CssSettings._

  GlobalRegistry.register(new GlobalStyles)
  val s = GlobalRegistry[GlobalStyles].get

  class GlobalStyles extends StyleSheet.Inline {
    import globalStyles.CssSettings._

    import dsl._

    val englishLavender = c"#b5838d"
    val apricot         = c"#ffcdb2"
    val peach           = c"#ffb4a2"
    val parrotPink      = c"#e5989b"
    val oldLavender     = c"#6d6875"

    val pageContainer = style(
      height(100 %%),
      backgroundColor(apricot)
    )

    val navButton = style(
      textDecoration := "none",
      backgroundColor(englishLavender),
      color.white,
      display.inlineBlock,
      textAlign.center,
      padding(15 px, 20 px),
      borderRadius(40 px),
      fontWeight.bold,
      &.hover - (
        color(englishLavender),
        backgroundColor(white)
      )
    )

  }
}
