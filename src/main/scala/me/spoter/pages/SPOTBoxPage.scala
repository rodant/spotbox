package me.spoter.pages

import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{Callback, Reusability, ScalaComponent}
import me.spoter.models.{IRI, Resource}
import me.spoter.services.ResourceService
import me.spoter.{Session, SessionTracker, StateXSession}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SPOTBoxBackend(bs: BackendScope[Unit, StateXSession[State]]) extends EntityListBackend(bs) {
  override protected val entityUriFragment: String = "path"
  override protected val entityRenderName: String = "Ressources"

  override protected def newEntity(): Resource = Resource()

  override protected def createEntity(sxs: StateXSession[State]): Callback = Callback.future {
    val newResource = sxs.state.newEntity.get.asInstanceOf[Resource].copy(iri = IRI(""))

    val createdResourceF = Future.successful()
    createdResourceF.flatMap { _ =>
      fetchEntities(sxs.session.get, forceLoad = true).map(s => bs.modState(_.copy(state = s)))
    }
  }

  override protected val deleteEntity: Option[IRI => Callback] = Some { iri =>
    Callback.future {
      val deletedF = Future.successful()
      deletedF.map { _ =>
        bs.modState(old => old.copy(state = old.state.copy(es = old.state.es.filter(_.iri != iri))))
      }
    }
  }

  private[pages] def fetchEntities(s: Session, forceLoad: Boolean = false): Future[State] =
    ResourceService.listFolder(IRI(s.webId).parent.parent).map(rs => State(rs))
}

object SPOTBoxPage extends SessionTracker[Unit, State, SPOTBoxBackend] {
  private val componentName: String = "SPOTBoxPage"

  private val component = ScalaComponent
    .builder[Unit](componentName)
    .initialState(StateXSession[State](State(Seq()), Some(initialSession)))
    .renderBackend[SPOTBoxBackend]
    .componentDidMount(c => trackSessionOn(s => c.backend.fetchEntities(s))(c))
    .componentWillUnmountConst(trackSessionOff())
    .configure(Reusability.shouldComponentUpdate)
    .build

  def apply(): VdomElement = component().vdomElement
}
