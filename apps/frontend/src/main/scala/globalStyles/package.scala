import scalacss.defaults.Exports
import scalacss.internal.mutable.Settings

package object globalStyles {
  val CssSettings: Exports with Settings =
    scalacss.devOrProdDefaults
}
