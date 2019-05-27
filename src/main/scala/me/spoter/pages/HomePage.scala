package me.spoter.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import me.spoter.components.bootstrap._
import scalacss.defaults.Exports
import scalacss.internal.mutable.Settings

object HomePage {
  // This will choose between dev/prod depending on your scalac `-Xelide-below` setting
  val CssSettings: Exports with Settings = scalacss.devOrProdDefaults

  private val uriParamLeft = "?path="
  private val initialState = uriParamLeft + "/"

  class Backend(bs: BackendScope[Null, String]) {

    def render(path: String): VdomElement = {
      Container(
        <.h1("SPOTBox"),

      )
    }
  }

  private val component =
    ScalaComponent.builder[Null]("HomePage")
      .initialState(initialState)
      .renderBackend[Backend]
      .build

  def apply(): VdomElement = component(null).vdomElement
}
