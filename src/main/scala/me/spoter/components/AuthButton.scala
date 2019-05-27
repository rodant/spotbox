package me.spoter.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import me.spoter.components.bootstrap.Button
import me.spoter.solid_libs.SolidAuth

import scala.scalajs.js

/**
  *
  */
object AuthButton {

  case class Props(popupUri: String, callbackUri: String, loggedIn: Boolean)

  private val component = ScalaComponent
    .builder[Props]("AuthButton")
    .renderBackend[Backend]
    .build

  class Backend(bs: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement = {
      if (!props.loggedIn)
        Button()(^.onClick --> login)("Einlogen")
      else
        Button()(^.onClick --> logout)("Ausloggen")
    }

    private def login(): Callback = bs.props.map { p =>
      val args = js.Dynamic.literal(popupUri = p.popupUri)
      SolidAuth.popupLogin(args)
      ()
    }

    private def logout(): Callback = bs.props.map { _ =>
      SolidAuth.logout()
      ()
    }
  }

  def apply(popupUri: String, callbackUri: String = null, loggedIn: Boolean = false): VdomElement =
    component(Props(popupUri, callbackUri, loggedIn)).vdomElement
}