package me.spoter.components.bootstrap

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{Children, JsComponent}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */

object FormGroup {

  @JSImport("react-bootstrap", "Form.Group")
  @js.native
  object RawComponent extends js.Object

  @js.native
  trait Props extends js.Object {
    var controlId: String = js.native
  }

  private def props(controlId: String): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.controlId = controlId
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](RawComponent)

  def apply(controlId: String)(children: ChildArg*): VdomElement =
    component(props(controlId))(children: _*).vdomElement
}
