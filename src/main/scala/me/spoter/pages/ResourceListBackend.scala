package me.spoter.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import me.spoter.StateXSession
import me.spoter.components._
import me.spoter.components.bootstrap._
import me.spoter.models.rdf.IRI
import me.spoter.models.{FSResource, Folder}
import me.spoter.services.ResourceService
import me.spoter.solid_libs.RDFHelper

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBufferView

case class State(rs: Iterable[FSResource], newFSResource: Option[FSResource] = None, uploading: Boolean = false)

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
                ^.title := "New Directory",
                ^.alignSelf := "center",
                ^.onClick --> bs.modState(old => old.copy(state = old.state.copy(newFSResource = Option(newFolder())))))
            },
            renderWhen(sxs.session.isDefined) {
              <.i(^.className := "fas fa-file-upload ui-elem action-icon",
                ^.title := "Upload file",
                ^.alignSelf := "center",
                ^.onClick --> showUpload(true))
            }
          )
        )
      ),
      renderWhen(sxs.state.uploading)(renderUploadDialog(props, sxs)),
      renderWhen(sxs.session.isEmpty) {
        <.h2("Please log in!")
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
    def parent(iri: IRI): IRI = if (ResourceService.isPod(iri) || iri == iri.parent) IRI.BlankNodeIRI else iri.parent

    def extendedLastPathComponent(iri: IRI): String = if (ResourceService.isPod(iri)) iri.removedTailingSlash.toString else iri.lastPathComponent

    @tailrec
    def toCompAndIRIs(cis: List[(String, IRI)]): List[(String, IRI)] = cis match {
      case Nil | (_, IRI.BlankNodeIRI) :: _ => cis
      case (_, iri) :: _ =>
        val (parentPathComp, parentIri) = (extendedLastPathComponent(parent(iri)), parent(iri))
        toCompAndIRIs((parentPathComp, parentIri) :: cis)
    }

    val rootIri = IRI(sxs.session.fold(IRI.BlankNodeIRI.innerUri)(_.webId)).root
    val pathCompIriPairs = props.iri.normalize match {
      case iri@IRI.BlankNodeIRI => List((iri.toString, rootIri))
      case iri => toCompAndIRIs((extendedLastPathComponent(iri), iri) :: Nil)
    }

    Breadcrumb(bsPrefix = "spoter-breadcrumb")(^.alignSelf := "center")(
      pathCompIriPairs.zipWithIndex.toVdomArray {
        case ((_, iri), 0) =>
          BreadcrumbItem(active = pathCompIriPairs.size == 1, href = "/")(
            ^.key := iri.toString)(<.i(^.alignSelf := "center", ^.className := "fas fa-home", ^.fontSize := "1.3em"),
            <.i(^.marginLeft := "0.2em", "ME"))
        case ((pc, iri), ind) =>
          BreadcrumbItem(active = ind == pathCompIriPairs.length - 1, href = s"#$resourceUriFragment?iri=$iri")(
            ^.key := iri.toString)(<.i(^.alignSelf := "center", pc))
      }
    )
  }

  private def renderUploadDialog(props: SPOTBox.Props, sxs: StateXSession[State]): VdomElement = {
    def confirmUpload()(e: ReactEvent): Callback = {
      Callback.empty
    }

    def onFilesChange(e: ReactEventFromInput): Callback = {
      import org.scalajs.dom.raw.FileReader

      e.persist()
      e.stopPropagation()
      e.preventDefault()
      val file = e.target.files(0)
      val promise = Promise[ArrayBufferView]()
      val reader = new FileReader()
      reader.onloadend = e => {
        if (e.loaded == file.size) {
          promise.success(reader.result.asInstanceOf[ArrayBufferView])
        } else {
          promise.failure(new Exception(s"Error reading the file: ${file.name}"))
        }
      }
      reader.readAsArrayBuffer(file)
      Callback.future {
        promise.future.flatMap { data =>
          val encodedName = js.Dynamic.global.encodeURI(file.name).toString
          val fileIri = props.iri.concatPath(encodedName)
          RDFHelper.uploadFile(fileIri, data, "image/png")
        }.map { _ =>
          RDFHelper.reloadAndSync(props.iri)
          showUpload(false)
        }
      }
    }

    Modal(size = "lg", show = true, onHide = (_: Unit) => showUpload(false))(
      ModalHeader(closeButton = true)(ModalTitle()("Upload File")),
      ModalBody()(
        <.div(
          <.p("Select the file to upload:"),
          Form(validated = true)(^.noValidate := true, ^.onSubmit ==> dismissOnSubmit)(
            FormControl(`type` = "file", onChange = onFilesChange(_))(^.autoFocus := true)
          )
        )),
      ModalFooter()(
        Button(variant = "secondary", onClick = (_: ReactEventFromInput) => showUpload(false))("Cancel"),
        Button(onClick = confirmUpload()(_))("Upload File")
      )
    )
  }

  private def onConfirm(): Callback = bs.state.zip(bs.props).flatMap[Unit] { case (sxs, props) =>
    sxs.state.newFSResource.fold(Callback.empty)(_ => createFSResource(props, sxs))
  }

  def showUpload(flag: Boolean): Callback = bs.modState(sxs => sxs.copy(state = sxs.state.copy(uploading = flag)))

  private def onCancel(): Callback = bs.modState(old => old.copy(state = old.state.copy(newFSResource = None)))

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