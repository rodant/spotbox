package me.spoter.components

import japgolly.scalajs.react.component.builder.Lifecycle
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaComponent}
import me.spoter.components.bootstrap._
import me.spoter.models.{BlankNodeEntity, IRI, SPOTEntity}

object EntityList {

  case class Props(entityUriFragment: String, es: Iterable[SPOTEntity], deleteHandler: Option[IRI => Callback])

  case class State(entityToDelete: Option[SPOTEntity] = None)

  private val component = ScalaComponent
    .builder[Props]("EntityList")
    .initialState(State())
    .renderP(($, P) => Form()(
      <.div(
        P.es.toTagMod(renderEntity($)),
        renderConfirmDeletion($))))
    .build

  def apply(entityUriFragment: String, es: Iterable[SPOTEntity], deleteHandler: Option[IRI => Callback] = None): VdomElement =
    component(Props(entityUriFragment, es, deleteHandler)).vdomElement

  private def renderEntity($: Lifecycle.RenderScope[Props, State, Unit])(e: SPOTEntity): VdomElement = {
    val uriFragment = $.props.entityUriFragment
    <.div(
      Row()(
        Col(xl = 4, lg = 4, md = 4, sm = 9, xs = 9)(
          NavLink(href = s"#$uriFragment?iri=${e.iri}")(e.name)
        ),
        Col(xl = 8, lg = 8, md = 8, sm = 3, xs = 3)(
          <.i(^.className := "fas fa-times",
            ^.title := "Löschen",
            ^.color := "red",
            ^.marginTop := 10.px,
            ^.marginLeft := 10.px,
            ^.verticalAlign := "bottom",
            ^.onClick --> $.modState(_.copy(entityToDelete = Some(e))))
        ).when($.props.deleteHandler.isDefined)
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
