package me.spoter.components

import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaComponent}
import me.spoter.components.bootstrap._
import me.spoter.models.{BlankNodeFSResource, Folder, FSResource}

object ResourceList {

  case class Props(resourceUriFragment: String, es: Iterable[FSResource], deleteHandler: Option[FSResource => Callback])

  case class State(resourceToDelete: Option[FSResource] = None)

  private val component = ScalaComponent
    .builder[Props]("ResourceList")
    .initialState(State())
    .renderP(($, P) => Form()(
      <.div(
        P.es.toTagMod(renderResource($)),
        renderConfirmDeletion($))))
    .build

  def apply(resourceUriFragment: String, es: Iterable[FSResource], deleteHandler: Option[FSResource => Callback] = None): VdomElement =
    component(Props(resourceUriFragment, es, deleteHandler)).vdomElement

  private def renderResource($: Lifecycle.RenderScope[Props, State, Unit])(e: FSResource): VdomElement = {
    val uriFragment = $.props.resourceUriFragment
    val folderIcon = <.i(^.alignSelf := "center", ^.color := "#F97B", ^.className := "fas fa-folder fa-2x")
    val fileIcon = <.i(^.alignSelf := "center", ^.color := "#0009", ^.className := "far fa-file-alt fa-2x")
    <.div(^.key := e.name,
      Row()(
        Col(xl = 10, lg = 10, md = 10, sm = 10, xs = 10)(
          <.div(^.display := "flex",
            e match {
              case Folder(_, _) => folderIcon
              case _ => fileIcon
            },
            NavLink(href = s"#$uriFragment?iri=${e.iri}")(e.name)
          )
        ),
        Col()(
          <.i(^.className := "fas fa-file-download ui-elem action-icon",
            ^.title := "Download",
            ^.onClick --> Callback.empty),
          $.props.deleteHandler.map { _ =>
            <.i(^.className := "far fa-trash-alt ui-elem action-icon",
              ^.title := "Löschen",
              ^.marginTop := 10.px,
              ^.onClick --> $.modState(_.copy(resourceToDelete = Some(e))))
          }
        )
      )
    )
  }

  private def renderConfirmDeletion($: Lifecycle.RenderScope[Props, State, Unit]): VdomElement = {
    val close = (_: Unit) => $.modState(_.copy(resourceToDelete = None))
    val resourceToDelete = $.state.resourceToDelete.getOrElse(BlankNodeFSResource)

    def confirmDeletion(e: ReactEventFromInput): Callback = {
      val deleteHandler = $.props.deleteHandler.get
      deleteHandler(resourceToDelete).flatMap(close)
    }

    Modal(size = "sm", show = resourceToDelete != BlankNodeFSResource, onHide = close)(
      ModalHeader(closeButton = true)(
        ModalTitle()("Ressource Entfernen")
      ),
      ModalBody()(
        <.p(s"Wollen Sie wirklich ${resourceToDelete.name} löschen?"),
        resourceToDelete match {
          case Folder(_, name) =>
            <.p(^.fontWeight.bold, s"$name ist ein Verzeichnis, alle Inhalte darunter werden gelöscht!")
          case _ => EmptyVdom
        }
      ),
      ModalFooter()(
        Button(onClick = confirmDeletion(_))("Löschen")
      )
    )
  }
}
