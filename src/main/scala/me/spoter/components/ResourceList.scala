package me.spoter.components

import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaComponent}
import me.spoter.components.bootstrap._
import me.spoter.models.{BlankNodeFSResource, FSResource, File, Folder}
import org.scalajs.dom.raw.{Blob, BlobPropertyBag, URL}

import scala.scalajs.js

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
    e match {
      case Folder(_, _) =>
        <.div(^.key := e.name,
          Row()(
            Col(xl = 10, lg = 10, md = 10, sm = 10, xs = 10)(
              <.div(^.display := "flex", folderIcon,
                NavLink(href = s"#$uriFragment?iri=${e.iri}")(e.name)
              )
            ),
            Col()(
              $.props.deleteHandler.map { _ =>
                <.i(^.className := "far fa-trash-alt ui-elem action-icon",
                  ^.title := "Delete",
                  ^.marginTop := 10.px,
                  ^.onClick --> $.modState(_.copy(resourceToDelete = Some(e))))
              }
            )
          )
        )
      case r =>
        val f = r.asInstanceOf[File]
        val options = BlobPropertyBag(f.`type`)
        val downloadURL = URL.createObjectURL(new Blob(js.Array(f.data.get), options))
        <.div(^.key := e.name,
          Row()(
            Col(xl = 10, lg = 10, md = 10, sm = 10, xs = 10)(
              <.div(^.display := "flex", fileIcon, NavLink(active = false)(e.name))
            ),
            Col()(
              <.a(^.href := downloadURL, ^.download := s"${e.name}",
                <.i(^.className := "fas fa-file-download ui-elem action-icon",
                  ^.title := "Download")),
              $.props.deleteHandler.map { _ =>
                <.i(^.className := "far fa-trash-alt ui-elem action-icon",
                  ^.title := "Delete",
                  ^.marginTop := 10.px,
                  ^.onClick --> $.modState(_.copy(resourceToDelete = Some(e))))
              }
            )
          )
        )
    }
  }

  private def renderConfirmDeletion($: Lifecycle.RenderScope[Props, State, Unit]): VdomElement = {
    val close = (_: Unit) => $.modState(_.copy(resourceToDelete = None))
    val resourceToDelete = $.state.resourceToDelete.getOrElse(BlankNodeFSResource)

    def confirmDeletion(e: ReactEventFromInput): Callback = {
      val deleteHandler = $.props.deleteHandler.get
      deleteHandler(resourceToDelete).flatMap(close)
    }

    Modal(show = resourceToDelete != BlankNodeFSResource, onHide = close)(
      ModalHeader(closeButton = true)(
        ModalTitle()("Delete Resource")
      ),
      ModalBody()(
        <.p(s"Do you really want to delete ${resourceToDelete.name}?"),
        resourceToDelete match {
          case Folder(_, name) =>
            <.p(^.fontWeight.bold, s"$name is a directory all contained resources will be deleted!")
          case _ => EmptyVdom
        }
      ),
      ModalFooter()(
        Button(onClick = confirmDeletion(_))("Delete")
      )
    )
  }
}
