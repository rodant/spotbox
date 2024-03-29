package me.spoter

import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{Callback, ReactEventFromInput}
import org.scalajs.dom.ext.KeyCode

package object components {
  def handleEsc(escHandler: () => Callback): PartialFunction[Int, Callback] = {
    case KeyCode.Escape => escHandler()
  }

  def handleEnter(enterHandler: () => Callback): PartialFunction[Int, Callback] = {
    case KeyCode.Enter => enterHandler()
  }

  def renderWhen(b: Boolean)(r: => VdomElement): Option[VdomElement] = if (b) Some(r) else None

  val ignoreKey: PartialFunction[Int, Callback] = {
    case _ => Callback(())
  }

  val dismissOnSubmit: ReactEventFromInput => Callback = e => Callback(e.preventDefault())

}
