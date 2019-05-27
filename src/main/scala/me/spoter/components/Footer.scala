package me.spoter.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._

object Footer {

  private val component = ScalaComponent.builder
    .static("Footer")(
      <.footer(
        ^.textAlign.center,
        ^.minHeight := 100.px,
        ^.backgroundColor := "#545b62",
        ^.color.white,
        <.div(^.borderBottom := "1px solid grey", ^.padding := "0px"),
        <.p(^.paddingTop := "5px", "Footer")
      )
    )
    .build

  def apply(): Unmounted[Unit, Unit, Unit] = component()
}
