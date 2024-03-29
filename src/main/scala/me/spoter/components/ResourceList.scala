package me.spoter.components

import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.vdom.Attr
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaComponent}
import me.spoter.components.bootstrap._
import me.spoter.models._
import me.spoter.solid_libs.RDFHelper
import org.scalajs.dom.experimental.Response
import org.scalajs.dom.html.Element
import org.scalajs.dom.{Blob, FileReader, window}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

object ResourceList {

  case class Props(resourceUriFragment: String, es: Iterable[FSResource], deleteHandler: Option[FSResource => Callback])

  case class State(resourceToDelete: Option[FSResource] = None, dataToOpen: Option[String] = None)

  private val component = ScalaComponent
    .builder[Props]("ResourceList")
    .initialState(State())
    .renderP(($, P) => Form()(
      <.div(
        P.es.toTagMod(renderResource($)),
        renderConfirmDeletion($),
        renderFileView($))))
    .build

  def apply(resourceUriFragment: String, es: Iterable[FSResource], deleteHandler: Option[FSResource => Callback] = None): VdomElement =
    component(Props(resourceUriFragment, es, deleteHandler)).vdomElement

  private def renderResource($: Lifecycle.RenderScope[Props, State, Unit])(e: FSResource): VdomElement = {
    def createDeleteBtn(r: FSResource): Option[VdomTagOf[Element]] =
      $.props.deleteHandler.map { _ =>
        <.i(^.className := "far fa-trash-alt ui-elem action-icon",
          ^.title := "Delete",
          ^.marginTop := 10.px,
          ^.onClick --> $.modState(_.copy(resourceToDelete = Some(r))))
      }

    def createDownloadBtn(f: File): VdomTagOf[Element] =
      <.a(^.download := s"${f.name}", ^.onClick ==> startDownload(f),
        <.i(^.className := "fas fa-file-download ui-elem action-icon", ^.title := "Download"))

    val uriFragment = $.props.resourceUriFragment
    val deleteBtn = Some(createDeleteBtn(e)).flatten
    val (resourceIcon, navLink, downloadButton, deleteButton) = e match {
      case POD(_, _) =>
        val podIcon = <.i(^.alignSelf := "center", ^.color := "#6C757D", ^.className := "fas fa-database fa-2x")
        (podIcon, NavLink(href = s"#$uriFragment?iri=${e.iri}")(e.name), None, None)
      case Folder(_, _) =>
        val folderIcon = <.i(^.alignSelf := "center", ^.color := "#F97B", ^.className := "fas fa-folder fa-2x")
        (folderIcon, NavLink(href = s"#$uriFragment?iri=${e.iri}")(e.name), None, deleteBtn)
      case r =>
        val f = r.asInstanceOf[File]
        val fileIcon = <.i(^.alignSelf := "center", ^.color := "#0009", ^.className := "far fa-file-alt fa-2x")
        (fileIcon, <.a(f.name, ^.className := "nav-link", ^.cursor := "pointer", ^.onClick ==> openResource(f, $)),
          Some(createDownloadBtn(f)), deleteBtn)
    }
    <.div(^.key := e.name,
      Row()(
        Col(xl = 10, lg = 10, md = 10, sm = 9, xs = 9)(<.div(^.display := "flex", resourceIcon, navLink)),
        Col()(deleteButton, downloadButton)
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

  private def renderFileView($: Lifecycle.RenderScope[Props, State, Unit]): Option[VdomElement] = {
    val close = (_: Unit) => $.modState(_.copy(dataToOpen = None))
    $.state.dataToOpen.map { data =>
      val mimeTypeRegex = "data:(.+);.+".r("mimeType")
      val mimeType = mimeTypeRegex.findFirstMatchIn(data.substring(0, 50)).map { m =>
        m.group("mimeType")
      }.getOrElse(File.defaultType)
      val contentView = mimeType match {
        case mt if mt.startsWith("audio/") => <.audio(^.src := data, ^.controls := true, ^.width := "100%")
        case mt if mt startsWith ("video/") => <.video(^.src := data, ^.controls := true, ^.width := "100%")
        case mt if mt.startsWith("image/") => <.img(^.src := data, ^.width := "100%")
        case File.defaultType => <.textarea(^.value := data, ^.width := "100%", ^.height := 500.px)
        case _ => <.`object`(Attr("data") := data, ^.`type` := mimeType, ^.width := "100%", ^.height := 500.px)
      }
      Modal(size = "lg", show = true, onHide = close)(
        ModalHeader(closeButton = true)(
          ModalTitle()("File View")
        ),
        ModalBody()(<.p(contentView))
      )
    }
  }

  private def startDownload(file: File)(e: ReactEventFromInput): Callback = Callback.future {
    e.preventDefault()
    e.stopPropagation()
    generateDataUrl(file).map(url => Callback(window.open(url.replaceFirst(":.+;", ":octet/stream;"))))
  }

  private def openResource(file: File, $: Lifecycle.RenderScope[Props, State, Unit])(e: ReactEventFromInput): Callback =
    Callback.future {
      e.preventDefault()
      e.stopPropagation()
      (if (file.`type`.startsWith("text/") || file.`type`.endsWith("/text")) readTextFile(file)
      else generateDataUrl(file))
        .map { data =>
          $.modState(old => old.copy(dataToOpen = Some(data)))
        }
    }

  private def generateDataUrl(f: File): Future[String] = readFileData(f)(reader => reader.readAsDataURL)

  private def readTextFile(f: File): Future[String] = readFileData(f)(reader => reader.readAsText(_))

  private def readFileData(f: File)(op: FileReader => Blob => Unit): Future[String] =
    RDFHelper.flatLoadEntity(f.iri.innerUri, forceLoad = true) {
      case res: Response =>
        val promise = Promise[String]()
        val reader = new FileReader()
        reader.onload = _ => promise.success(reader.result.asInstanceOf[String])
        reader.onerror = _ => promise.failure(new Exception(s"Error reading the file for data URL: ${f.name}"))
        res.blob().toFuture.flatMap { blob =>
          op(reader)(blob)
          promise.future
        }
    }
}
