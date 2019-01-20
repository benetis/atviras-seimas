import scalacss.defaults.Exports
import scalacss.internal.mutable.Settings

package object styles {
  val CssSettings: Exports with Settings = scalacss.devOrProdDefaults
}
