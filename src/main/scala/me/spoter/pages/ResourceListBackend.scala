package me.spoter.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import me.spoter.StateXSession
import me.spoter.components._
import me.spoter.components.bootstrap._
import me.spoter.models.rdf.IRI
import me.spoter.models.{FSResource, Folder}

case class State(rs: Iterable[FSResource], newFSResource: Option[FSResource] = None)

abstract class ResourceListBackend(bs: BackendScope[SPOTBox.Props, StateXSession[State]]) {
  protected val resourceUriFragment: String
  protected val resourceRenderName: String

  protected val deleteFSResource: Option[FSResource => Callback] = None

  protected def newFolder(): Folder

  protected def createFSResource(props: SPOTBox.Props, sxs: StateXSession[State]): Callback

  def render(props: SPOTBox.Props, sxs: StateXSession[State]): VdomElement = {
    val rs = sxs.state.rs
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
              <.i(^.className := "fas fa-folder-plus ui-elem action-icon",
                ^.title := "Neuer Ordner",
                ^.alignSelf := "center",
                ^.onClick --> bs.modState(old => old.copy(state = old.state.copy(newFSResource = Option(newFolder())))))
            },
            renderWhen(sxs.session.isDefined) {
              <.i(^.className := "fas fa-file-upload ui-elem action-icon",
                ^.title := "Datei hochladen",
                ^.alignSelf := "center",
                ^.onClick --> Callback.empty)
            }
          )
        )
      ),
      renderWhen(sxs.session.isEmpty) {
        <.h2("Bitte einloggen!")
      },
      sxs.session.flatMap { _ =>
        sxs.state.newFSResource.map { e =>
          Row()(
            Form(validated = true)(^.noValidate := true, ^.onSubmit ==> dismissOnSubmit)(
              WithConfirmAndCancel(() => onConfirm(), () => onCancel(), show = false)(
                FormControl(value = e.name, size = "sm", onChange = onChangeName(_))(
                  ^.placeholder := "Name", ^.autoFocus := true, ^.required := true, ^.maxLength := 40,
                  ^.onKeyUp ==> handleKey)(),
              )
            )
          )
        }
      },
      renderWhen(sxs.session.isDefined) {
        ResourceList(resourceUriFragment, rs, deleteFSResource)
      }
    )
  }

  private def renderBreadcrumb(props: SPOTBox.Props, sxs: StateXSession[State]): VdomElement = {
    def toCompAndIRIs(cis: List[(String, IRI)]): List[(String, IRI)] = cis match {
      case (_, iri) :: _ if iri == iri.parent => cis
      case (_, iri) :: _ => toCompAndIRIs((iri.parent.lastPathComponent, iri.parent) :: cis)
    }

    val pathCompIriPairs = props.iri.normalize match {
      case IRI.BlankNodeIRI => List(("", IRI(sxs.session.fold(IRI.BlankNodeIRI.innerUri)(_.webId)).root))
      case iri => toCompAndIRIs((iri.lastPathComponent, iri) :: Nil)
    }

    Breadcrumb(bsPrefix = "spoter-breadcrumb")(^.alignSelf := "center")(
      pathCompIriPairs.zipWithIndex.toVdomArray {
        case ((_, iri), 0) =>
          BreadcrumbItem(active = pathCompIriPairs.size == 1, href = s"#$resourceUriFragment?iri=$iri")(
            ^.key := iri.toString)(<.i(^.alignSelf := "center", ^.className := "fas fa-home", ^.fontSize := "1.3em"),
            <.i(^.marginLeft := "0.2em", "ME"))
        case ((pc, iri), ind) =>
          BreadcrumbItem(active = ind == pathCompIriPairs.length - 1, href = s"#$resourceUriFragment?iri=$iri")(
            ^.key := iri.toString)(<.i(^.alignSelf := "center", pc))
      }
    )
  }

  private def onConfirm(): Callback = bs.state.zip(bs.props).flatMap[Unit] { case (sxs, props) =>
    sxs.state.newFSResource.fold(Callback.empty)(_ => createFSResource(props, sxs))
  }

  private def onCancel(): Callback = bs.modState(old => old.copy(state = old.state.copy(newFSResource = None)))

  private def renderWhen(b: Boolean)(r: => VdomElement): Option[VdomElement] = if (b) Some(r) else None

  private def onChangeName(e: ReactEventFromInput): Callback = {
    e.persist()
    bs.modState(old =>
      old.copy(state =
        old.state.copy(newFSResource =
          old.state.newFSResource.map(g => g.withNewName(e.target.value)))))
  }

  private def handleKey(e: ReactKeyboardEvent): Callback =
    handleEsc(onCancel _).orElse(handleEnter(onConfirm _)).orElse(ignoreKey)(e.keyCode)
}