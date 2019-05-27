package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object InputGroup extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "InputGroup")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(size: js.UndefOr[String] = js.undefined): WithProps = auto
}

object InputGroupAppend extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "InputGroup.Append")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(): WithProps = auto
}
