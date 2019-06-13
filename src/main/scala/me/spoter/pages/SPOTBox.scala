package me.spoter.pages

import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.component.builder.Lifecycle.ComponentWillReceiveProps
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{Callback, Reusability, ScalaComponent}
import me.spoter.models.{IRI, Resource}
import me.spoter.services.ResourceService
import me.spoter.{Session, SessionTracker, StateXSession}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SPOTBox {

  case class Props(iri: IRI)

  class Backend(bs: BackendScope[Props, StateXSession[State]]) extends EntityListBackend(bs) {
    override protected val entityUriFragment: String = "explorer"
    override protected val entityRenderName: String = "Ressources"

    override protected def newEntity(): Resource = Resource(name = "")

    override protected def createEntity(props: Props, sxs: StateXSession[State]): Callback = Callback.future {
      val createdResourceF = ResourceService.createFolder(props.iri, sxs.state.newEntity.get.name)
      createdResourceF.flatMap { _ =>
        fetchEntities(props, sxs.session.get, forceLoad = true).map(s => bs.modState(_.copy(state = s)))
      }
    }

    override protected val deleteEntity: Option[IRI => Callback] = Some { iri =>
      Callback.future {
        ResourceService.deleteResource(iri).map { _ =>
          bs.modState(old => old.copy(state = old.state.copy(es = old.state.es.filter(_.iri != iri))))
        }
      }
    }

    private[pages] def fetchEntities(props: Props, s: Session, forceLoad: Boolean = false): Future[State] = {
      val effectiveIRI = if (props.iri == IRI.BlankNodeIRI) IRI(s.webId).parent.parent else props.iri
      ResourceService.listFolder(effectiveIRI, forceLoad = forceLoad).map { rs =>
        val resourceOrd = new Ordering[Resource] {
          override def compare(x: Resource, y: Resource): Int = (x, y) match {
            case (Resource(_, _, true), Resource(_, _, false)) => -1
            case (Resource(_, _, false), Resource(_, _, true)) => 1
            case (Resource(_, nx, _), Resource(_, ny, _)) => Ordering.String.compare(nx, ny)
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
