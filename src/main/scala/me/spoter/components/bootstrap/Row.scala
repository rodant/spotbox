package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */

object Row extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Row")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(): WithProps = auto
}
