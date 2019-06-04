package me.spoter.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import me.spoter.StateXSession
import me.spoter.components._
import me.spoter.components.bootstrap.{Col, Container, Form, FormControl, Row}
import me.spoter.models.{IRI, Resource}

case class State(es: Iterable[Resource], newEntity: Option[Resource] = None)

abstract class EntityListBackend(bs: BackendScope[Unit, StateXSession[State]]) {
  protected val entityUriFragment: String
  protected val entityRenderName: String

  protected val deleteEntity: Option[IRI => Callback] = None

  protected def newEntity(): Resource

  protected def createEntity(sxs: StateXSession[State]): Callback

  def render(sxs: StateXSession[State]): VdomElement = {
    val es = sxs.state.es
    Container(
      Row()(
        Col(xl = 4, lg = 4, md = 4, sm = 9, xs = 9)(
          <.div(^.display := "flex",
            <.i(^.color := "#F97B", ^.className := "fas fa-folder-open fa-2x"),
            <.div(^.alignSelf := "center", "SPOT/")
          )
        ),
        Col()(
          renderWhen(sxs.session.isDefined) {
            <.i(^.className := "fas fa-plus-circle fa-2x",
              ^.title := "Neu Anlegen",
              ^.cursor := "pointer",
              ^.color := "#007bff",
              ^.marginLeft := 30.px,
              ^.onClick --> bs.modState(old => old.copy(state = old.state.copy(newEntity = Option(newEntity())))))
          }
        )
      ),
      renderWhen(sxs.session.isEmpty) {
        <.h2("Bitte einloggen!")
      },
      sxs.session.flatMap { _ =>
        sxs.state.newEntity.map { e =>
          Row()(
            Form(validated = true)(^.noValidate := true)(
              WithConfirmAndCancel(() => onConfirm(), () => onCancel())(
                FormControl(value = e.name, onChange = onChangeName(_))(
                  ^.placeholder := "Name", ^.autoFocus := true, ^.required := true, ^.maxLength := 40,
                  ^.onKeyUp ==> handleKey)(),
              )
            )
          )
        }
      },
      renderWhen(sxs.session.isDefined) {
        EntityList(entityUriFragment, es, deleteEntity)
      }
    )
  }

  private def onConfirm(): Callback = bs.state.flatMap[Unit] { state =>
    if (state.state.newEntity.get.name.isEmpty)
      Callback()
    else
      createEntity(state)
  }

  private def onCancel(): Callback = bs.modState(old => old.copy(state = old.state.copy(newEntity = None)))

  private def renderWhen(b: Boolean)(r: => VdomElement): Option[VdomElement] = if (b) Some(r) else None

  private def onChangeName(e: ReactEventFromInput): Callback = {
    e.persist()
    bs.modState(old =>
      old.copy(state =
        old.state.copy(newEntity =
          old.state.newEntity.map(g => g.withNewName(e.target.value)))))
  }

  private def handleKey(e: ReactKeyboardEvent): Callback =
  //TODO: the enter key in the name field is causing a weird runtime error, but this code is correct.
  //Check later on if the error persists.
    handleEsc(onCancel).orElse(handleEnter(onConfirm)).orElse(ignoreKey)(e.keyCode)
}