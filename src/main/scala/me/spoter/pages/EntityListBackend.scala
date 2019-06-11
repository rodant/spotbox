package me.spoter.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import me.spoter.StateXSession
import me.spoter.components._
import me.spoter.components.bootstrap._
import me.spoter.models.{IRI, Resource}

case class State(es: Iterable[Resource], newEntity: Option[Resource] = None)

abstract class EntityListBackend(bs: BackendScope[SPOTBox.Props, StateXSession[State]]) {
  protected val entityUriFragment: String
  protected val entityRenderName: String

  protected val deleteEntity: Option[IRI => Callback] = None

  protected def newEntity(): Resource

  protected def createEntity(props: SPOTBox.Props, sxs: StateXSession[State]): Callback

  def render(props: SPOTBox.Props, sxs: StateXSession[State]): VdomElement = {
    val es = sxs.state.es
    Container(
      Row()(^.borderBottom := "1px lightgrey solid", ^.paddingBottom := 5.px)(
        Col(xl = 10, lg = 10, md = 10, sm = 10, xs = 10)(
          <.div(^.display := "flex",
            <.i(^.color := "#F97B", ^.alignSelf := "center", ^.className := "fas fa-folder-open fa-2x ui-elem"),
            <.div(^.display := "flex", renderBreadcrumb(props, sxs))
          )
        ),
        Col()(
          <.div(^.display := "flex", ^.height := "100%",
            renderWhen(sxs.session.isDefined) {
              <.i(^.className := "fas fa-folder-plus ui-elem",
                ^.title := "Neuer Ordner",
                ^.alignSelf := "center",
                ^.fontSize := "1.3em",
                ^.cursor := "pointer",
                ^.onClick --> bs.modState(old => old.copy(state = old.state.copy(newEntity = Option(newEntity())))))
            },
            renderWhen(sxs.session.isDefined) {
              <.i(^.className := "fas fa-file-upload ui-elem",
                ^.title := "Datei hochladen",
                ^.alignSelf := "center",
                ^.fontSize := "1.3em",
                ^.cursor := "pointer",
                ^.onClick --> Callback.empty)
            }
          )
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

  private def renderBreadcrumb(props: SPOTBox.Props, sxs: StateXSession[State]): VdomElement = {
    def toCompAndIRIs(cis: List[(String, IRI)]): List[(String, IRI)] = cis match {
      case (_, iri) :: _ if iri == iri.parent => cis
      case (_, iri) :: _ => toCompAndIRIs((iri.parent.lastPathComponent, iri.parent) :: cis)
    }

    val pathCompIriPairs = props.iri.normalize match {
      case IRI.BlankNodeIRI => List(("", IRI(sxs.session.get.webId).parent.parent))
      case iri => toCompAndIRIs((iri.lastPathComponent, iri) :: Nil)
    }

    Breadcrumb(bsPrefix = "spoter-breadcrumb")(^.alignSelf := "center")(
      pathCompIriPairs.zipWithIndex.toVdomArray {
        case ((pc, iri), 0) =>
          BreadcrumbItem(active = pathCompIriPairs.size == 1, href = s"#$entityUriFragment?iri=$iri")(
            ^.key := iri.toString)(<.i(^.alignSelf := "center", ^.className := "fas fa-home", ^.fontSize := "1.3em"))
        case ((pc, iri), ind) =>
          BreadcrumbItem(active = ind == pathCompIriPairs.length - 1, href = s"#$entityUriFragment?iri=$iri")(
            ^.key := iri.toString)(<.i(^.alignSelf := "center", pc))
      }
    )
  }

  private def onConfirm(): Callback = bs.state.zip(bs.props).flatMap[Unit] { case (state, props) =>
    if (state.state.newEntity.get.name.isEmpty)
      Callback()
    else
      createEntity(props, state)
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