package components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.~=>
import japgolly.scalajs.react.vdom.html_<^._
import me.benetis.shared.{
  MdsPointWithAdditionalInfo,
  MdsResult,
  MdsResultId
}
import org.scalajs.dom.raw.HTMLSelectElement
import scala.scalajs.js.Date
import scalacss.ScalaCssReact.scalacssStyleaToTagMod
import scalacss.internal.mutable.GlobalRegistry

object ChartDateRange {

  import globalStyles.CssSettings._

  GlobalRegistry.register(new Style)
  val styles = GlobalRegistry[Style].get

  case class Props(
    selectedMdsId: Option[MdsResultId],
    mdsList: Vector[MdsResult[MdsPointWithAdditionalInfo]],
    onChange: MdsResultId ~=> Callback)

  private val component = ScalaComponent
    .builder[Props]("Chart date range")
    .stateless
    .renderBackend[Backend]
    .build

  class Backend($ : BackendScope[Props, Unit]) {

    private def rangeDateFormat(
      mdsResult: MdsResult[MdsPointWithAdditionalInfo]
    ): String = {

      def appendZeros(value: Int): String =
        if (value < 9) s"0$value" else value.toString

      def dateToFormat(date: Date): String = {

        s"${date.getFullYear()}-${appendZeros(
          date.getMonth() + 1
        )}-${appendZeros(date.getDay())}"
      }

      val from = new Date(
        mdsResult.rangeFrom.range_from.millis
      )

      val to = new Date(mdsResult.rangeTo.range_to.millis)
      s"${dateToFormat(from)}-${dateToFormat(to)}"
    }

    def render(
      p: Props,
      s: Unit
    ) = {

      <.div(
        styles.container,
        <.select(
          ^.onChange ==> { e =>
            p.onChange(
              MdsResultId(
                e.target
                  .asInstanceOf[HTMLSelectElement]
                  .value
                  .toInt
              )
            )
          },
          p.mdsList
            .sortBy(_.rangeFrom.range_from.millis)
            .map(
              result =>
                <.option(
                  ^.value := result.id
                    .getOrElse(MdsResultId(0))
                    .id,
                  rangeDateFormat(result)
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
      width(100 %%)
    )

  }
}
