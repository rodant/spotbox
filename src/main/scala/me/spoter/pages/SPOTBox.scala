package me.spoter.pages

import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.component.builder.Lifecycle.ComponentWillReceiveProps
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{Callback, Reusability, ScalaComponent}
import me.spoter.models.rdf.IRI
import me.spoter.models.{FSResource, File, Folder}
import me.spoter.services.ResourceService
import me.spoter.{Session, SessionTracker, StateXSession}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SPOTBox {

  case class Props(iri: IRI)

  class Backend(bs: BackendScope[Props, StateXSession[State]]) extends ResourceListBackend(bs) {
    override protected val resourceUriFragment: String = "explorer"
    override protected val resourceRenderName: String = "Ressources"

    override protected def newFolder(): Folder = Folder()

    override protected def createFSResource(props: Props, sxs: StateXSession[State]): Callback = Callback.future {
      val createdResourceF = ResourceService.createFolder(props.iri, sxs.state.newFSResource.get.name)
      createdResourceF.flatMap { _ =>
        fetchEntities(props, sxs.session.get, forceLoad = true).map(s => bs.modState(_.copy(state = s)))
      }
    }

    override protected val deleteFSResource: Option[FSResource => Callback] = Some { resource =>
      Callback.future {
        ResourceService.deleteResource(resource).map { _ =>
          bs.modState(old => old.copy(state = old.state.copy(rs = old.state.rs.filter(_.iri != resource.iri))))
        }
      }
    }

    override def fetchEntities(props: Props, s: Session, forceLoad: Boolean = false): Future[State] = {
      val (effectiveIRI, isWebId) = if (props.iri == IRI.BlankNodeIRI) (IRI(s.webId), true) else (props.iri, false )
      ResourceService.listFolder(effectiveIRI, isWebId = isWebId, forceLoad = forceLoad).map { rs =>
        val resourceOrd = new Ordering[FSResource] {
          override def compare(x: FSResource, y: FSResource): Int = (x, y) match {
            case (Folder(_, _), File(_, _)) => -1
            case (File(_, _), Folder(_, _)) => 1
            case (e1, e2) => Ordering.by((e: FSResource) => e.name).compare(e1, e2)
          }
        }

        val sortedRes = rs.toArray.sorted(resourceOrd)
        State(sortedRes)
      }
    }
  }

  object Page extends SessionTracker[Props, State, Backend] {

    private val componentName: String = "SPOTBoxPage"

    private val component = ScalaComponent
      .builder[Props](componentName)
      .initialState(StateXSession[State](State(Seq()), Some(initialSession)))
      .renderBackend[Backend]
      .componentWillReceiveProps(handleNextProps)
      .componentDidMount(c => trackSessionOn(s => c.backend.fetchEntities(c.props, s))(c))
      .componentWillUnmountConst(trackSessionOff())
      .configure(Reusability.shouldComponentUpdate)
      .build

    private def handleNextProps(c: ComponentWillReceiveProps[Props, StateXSession[State], Backend]): Callback =
      Callback.future {
        c.backend.fetchEntities(c.nextProps, c.state.session.getOrElse(Session(IRI.BlankNodeIRI.innerUri)), forceLoad = true)
          .map { s =>
            c.modState(old => old.copy(state = s))
          }
      }

    def apply(iriS: String): VdomElement = component(Props(IRI(iriS))).vdomElement
  }

}
