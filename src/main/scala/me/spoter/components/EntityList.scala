package me.spoter.components

import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaComponent}
import me.spoter.components.bootstrap._
import me.spoter.models.{BlankNodeEntity, IRI, Resource}

object EntityList {

  case class Props(entityUriFragment: String, es: Iterable[Resource], deleteHandler: Option[IRI => Callback])

  case class State(entityToDelete: Option[Resource] = None)

  private val component = ScalaComponent
    .builder[Props]("EntityList")
    .initialState(State())
    .renderP(($, P) => Form()(
      <.div(
        P.es.toTagMod(renderEntity($)),
        renderConfirmDeletion($))))
    .build

  def apply(entityUriFragment: String, es: Iterable[Resource], deleteHandler: Option[IRI => Callback] = None): VdomElement =
    component(Props(entityUriFragment, es, deleteHandler)).vdomElement

  private def renderEntity($: Lifecycle.RenderScope[Props, State, Unit])(e: Resource): VdomElement = {
    val uriFragment = $.props.entityUriFragment
    val folderIcon = <.i(^.alignSelf := "center", ^.color := "#F97B", ^.className := "fas fa-folder fa-2x")
    val fileIcon = <.i(^.alignSelf := "center", ^.color := "#0009", ^.className := "far fa-file-alt fa-2x")
    <.div(^.key := e.name,
      Row()(
        Col(xl = 10, lg = 10, md = 10, sm = 10, xs = 10)(
          <.div(^.display := "flex",
            if (e.isFolder) folderIcon else fileIcon,
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
              ^.onClick --> $.modState(_.copy(entityToDelete = Some(e))))
          }
        )
      )
    )
  }

  private def renderConfirmDeletion($: Lifecycle.RenderScope[Props, State, Unit]): VdomElement = {
    val close = (_: Unit) => $.modState(_.copy(entityToDelete = None))
    val entityToDelete = $.state.entityToDelete.getOrElse(BlankNodeEntity)

    def confirmDeletion(e: ReactEventFromInput): Callback = {
      val deleteHandler = $.props.deleteHandler.get
      val iri = entityToDelete.iri
      deleteHandler(iri).flatMap(close)
    }

    Modal(size = "sm", show = entityToDelete != BlankNodeEntity, onHide = close)(
      ModalHeader(closeButton = true)(
        ModalTitle()("Ressource Entfernen")
      ),
      ModalBody()(s"Wollen Sie wirklich ${entityToDelete.name} löschen?"),
      ModalFooter()(
        Button(onClick = confirmDeletion(_))("Löschen")
      )
    )
  }
}
