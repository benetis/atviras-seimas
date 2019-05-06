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

    val emptyStyle = style()

    val englishLavender = c"#ff967c"
    val lightApricot    = c"#ffcdb2"
    val apricot         = c"#ffb48c"
    val peach           = c"#b56776"
    val parrotPink      = c"#e57578"
    val oldLavender     = c"#605575"
    val darkerLavender  = c"#322c3d"

    val fontColorOnDark  = c"#f1f1f2"
    val fontColorOnDark2 = c"#c9c8cc"

    val gray1    = c"#7a7581"
    val darkGray = c"#504c56"

    val bitBorderRadius = 5 px

    val pageContainer = style(
      backgroundColor(oldLavender),
      color(fontColorOnDark),
      height(100 vh)
    )

    val commonButtonStyles = style(
      backgroundColor(parrotPink),
      &.hover - (
        color(parrotPink),
        backgroundColor(white),
        cursor.pointer
      ),
      fontWeight.bold,
      color.white,
      textAlign.center
    )

    val navButton = commonButtonStyles + style(
      textDecoration := "none",
      display.inlineBlock,
      padding(15 px, 20 px),
      borderRadius(40 px)
    )

    val inputBox = style(
      flexGrow(2),
      borderTopLeftRadius(bitBorderRadius),
      borderBottomLeftRadius(
        bitBorderRadius
      ),
      padding(8 px, 16 px),
      outline.none,
      border.none
    )

  }
}
