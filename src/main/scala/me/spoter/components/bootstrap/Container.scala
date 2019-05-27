package me.spoter.components.bootstrap

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{Children, JsComponent}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */

object Container {

  @JSImport("react-bootstrap", "Container")
  @js.native
  object RawComponent extends js.Object

  val component = JsComponent[Null, Children.Varargs, Null](RawComponent)

  def apply(children: ChildArg*): VdomElement = component(children: _*).vdomElement
}
