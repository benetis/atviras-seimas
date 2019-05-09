package components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.~=>
import japgolly.scalajs.react.vdom.html_<^._
import me.benetis.shared.encoding.VoteEncoding
import me.benetis.shared.{
  KMeansId,
  KMeansResult,
  MdsPointWithAdditionalInfo,
  MdsResult,
  MdsResultId
}
import org.scalajs.dom.raw.HTMLSelectElement
import scala.scalajs.js.Date
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry

object ChartSelectorForKMeans {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Props(
    selectedKMeans: Option[KMeansId],
    kMeansList: Vector[KMeansResult],
    onChange: KMeansId ~=> Callback)

  private val component = ScalaComponent
    .builder[Props]("Chart selector")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props, Unit]) {

    def render(
      p: Props,
      s: Unit
    ) = {

      def toString(result: KMeansResult): String = {
        s"k=${result.totalClusters.total_clusters}, E=${VoteEncoding
          .encode(result.encoding)}"
      }

      <.div(
        <.span(styles.label, "Parametrai"),
        styles.container,
        <.select(
          styles.inputBox,
          ^.onChange ==> { e =>
            p.onChange(
              KMeansId(
                e.target
                  .asInstanceOf[HTMLSelectElement]
                  .value
                  .toInt
              )
            )
          },
          p.kMeansList
            .map(
              result =>
                <.option(
                  ^.value := result.id
                    .getOrElse(KMeansId(0))
                    .id,
                  toString(result)
                )
            )
            .toTagMod
        )
      )
    }
  }

  def apply(props: Props) = component(props)

  class Style extends StyleSheet.Inline {

    import dsl._

    val container = style(
      width(100 %%),
      marginBottom(5 px)
    )

    val label = style(
      marginRight(5 px)
    )

    val inputBox = globalStyles.s.inputBox + style(
      borderTopRightRadius(globalStyles.s.bitBorderRadius),
      borderBottomRightRadius(
        globalStyles.s.bitBorderRadius
      ),
      backgroundColor(white)
    )

  }
}
