package me.spoter.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, CallbackTo, ScalaComponent}
import japgolly.scalajs.react.extra.{Listenable, OnUnmount}

/**
  *
  */
object LoadingIndicator {

  case class Props()

  case class State(loadingCount: Int)

  sealed trait Message

  class Backend(bs: BackendScope[Props, State])  extends OnUnmount {
    def render(state: State): VdomElement = <.div(
      renderWhen(state.loadingCount > 0)(
        <.img(^.src := "/public/spotpod/images/3x-intersection-loading.gif", ^.className := "loading-indicator")
      )
    )
  }

  object LoadingListenable extends Listenable[Message] {
    override def register(listener: Message => Callback): CallbackTo[Callback] = ???
  }

  private val component = ScalaComponent
    .builder[Props]("LoadingIndicator")
    .initialState(State(0))
    .renderBackend[Backend]
    .configure(Listenable.listen(_ => LoadingListenable, c => (msg: Message) => Callback.empty))
    .build

  def apply(): VdomElement = component(Props()).vdomElement
}
