package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}
import japgolly.scalajs.react.Callback

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object Modal extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Modal")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(size: js.UndefOr[String] = js.undefined,
            show: js.UndefOr[Boolean] = js.undefined,
            centered: js.UndefOr[Boolean] = js.undefined,
            onHide: js.UndefOr[Unit => Callback] = js.undefined): WithProps = auto
}

object ModalHeader extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Modal.Header")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(closeButton: js.UndefOr[Boolean] = js.undefined): WithProps = auto
}

object ModalTitle extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Modal.Title")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(): WithProps = auto
}

object ModalBody extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "ModalBody")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(): WithProps = auto
}

object ModalFooter extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "ModalFooter")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(): WithProps = auto
}

