package me.spoter.components.solid

import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{Children, JsComponent}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */
object Value {

  @JSImport("@solid/react", "Value")
  @js.native
  object RawComponent extends js.Object

  @js.native
  trait Props extends js.Object {
    var src: String = js.native
  }

  private def props(src: String): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.src = src
    p
  }

  val component = JsComponent[Props, Children.None, Null](RawComponent)

  def apply(src: String): VdomElement = component(props(src)).vdomElement
}
