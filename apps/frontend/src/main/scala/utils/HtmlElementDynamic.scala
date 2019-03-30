package utils

import japgolly.scalajs.react.vdom.{
  HtmlAttrAndStyles,
  HtmlAttrs
}
import org.scalajs.dom.raw.HTMLElement
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.language.dynamics

@JSGlobal
@js.native
class HtmlElementDynamic extends HTMLElement {}
