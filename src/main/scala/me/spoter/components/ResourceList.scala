package me.spoter.components

import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaComponent}
import me.spoter.components.bootstrap._
import me.spoter.models.{BlankNodeFSResource, FSResource, File, Folder}
import me.spoter.solid_libs.RDFHelper
import org.scalajs.dom.experimental.Response
import org.scalajs.dom.{FileReader, window}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise

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
    val (resourceIcon, navLink, downloadButton) = e match {
      case Folder(_, _) => (folderIcon, NavLink(href = s"#$uriFragment?iri=${e.iri}")(e.name), None)
      case r =>
        val f = r.asInstanceOf[File]
        (fileIcon, NavLink(active = false)(e.name),
          Some(<.a(^.download := s"${e.name}", ^.onClick ==> startDownload(f),
            <.i(^.className := "fas fa-file-download ui-elem action-icon", ^.title := "Download"))))
    }
    <.div(^.key := e.name,
      Row()(
        Col(xl = 10, lg = 10, md = 10, sm = 10, xs = 10)(<.div(^.display := "flex", resourceIcon, navLink)),
        Col()(
          $.props.deleteHandler.map { _ =>
            <.i(^.className := "far fa-trash-alt ui-elem action-icon",
              ^.title := "Delete",
              ^.marginTop := 10.px,
              ^.onClick --> $.modState(_.copy(resourceToDelete = Some(e))))
          },
          downloadButton
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

  private def startDownload(file: File)(e: ReactEventFromInput): Callback = Callback.future {
    e.preventDefault()
    e.stopPropagation()
    RDFHelper.flatLoadEntity(file.iri.innerUri, forceLoad = true) {
      case res: Response =>
        val promise = Promise[String]
        val reader = new FileReader()
        reader.onload = _ => promise.success(reader.result.asInstanceOf[String])
        reader.onerror = _ => promise.failure(new Exception(s"Error reading the file download URL: ${file.name}"))
        res.blob().toFuture.flatMap { blob =>
          reader.readAsDataURL(blob)
          promise.future
        }.map(url => Callback(window.open(url.replaceFirst(":.+;", ":octet/stream;"))))
    }
  }
}
