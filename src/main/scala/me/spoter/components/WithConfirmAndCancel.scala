package me.spoter.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, CtorType, ScalaComponent}
import me.spoter.components.bootstrap.Row

/**
  * Component adding a confirm and a cancel button around any other child component.
  */
object WithConfirmAndCancel {

  case class Props(onConfirm: () => Callback, onCancel: () => Callback, show: Boolean)

  private val component = ScalaComponent
    .builder[Props]("FieldActionsButtons")
    .render_PC { (props, children) =>
      <.div(
        children,
        Row()(
          <.div(^.marginTop := 10.px,
            <.i(^.className := "fas fa-check fa-lg",
              ^.title := "Bestätigen",
              ^.color := "darkseagreen",
              ^.marginLeft := 10.px,
              ^.onClick --> props.onConfirm()),
            <.i(^.className := "fas fa-times fa-lg",
              ^.title := "Abbrechen",
              ^.color := "red",
              ^.marginLeft := 10.px,
              ^.onClick --> props.onCancel())
          ).when(props.show)))
    }.build

  def apply(onConfirm: () => Callback, onCancel: () => Callback, show: Boolean = true)
           (children: CtorType.ChildArg): VdomElement =
    component(Props(onConfirm, onCancel, show))(children).vdomElement

}
