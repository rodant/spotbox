package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}
import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{Callback, Children, JsComponent}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */

object Carousel extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Carousel")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(activeIndex: js.UndefOr[Int] = js.undefined,
            interval: js.Any = 5000,
            onSelect: js.UndefOr[(js.Any, String, js.Object) => Callback] = js.undefined): WithProps = auto
}

object CarouselItem {

  @JSImport("react-bootstrap", "Carousel.Item")
  @js.native
  object RawComponent extends js.Object

  val component = JsComponent[Null, Children.Varargs, Null](RawComponent)

  def apply(children: ChildArg*): VdomElement = component(children: _*).vdomElement
}

object CarouselCaption {

  @JSImport("react-bootstrap", "Carousel.Caption")
  @js.native
  object RawComponent extends js.Object

  val component = JsComponent[Null, Children.Varargs, Null](RawComponent)

  def apply(children: ChildArg*): VdomElement = component(children: _*).vdomElement
}
