package me.spoter.components.bootstrap

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{Children, JsComponent}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */

object FormLabel {

  @JSImport("react-bootstrap", "Form.Label")
  @js.native
  object RawComponent extends js.Object

  @js.native
  trait Props extends js.Object {
    var column: Option[Boolean] = js.native
  }

  private def props(column: Boolean): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.column = if (column) Some(column) else None
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](RawComponent)

  def apply(column: Boolean = false)(children: ChildArg*): VdomElement =
    component(props(column))(children: _*).vdomElement
}
