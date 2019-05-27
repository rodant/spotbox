package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */

object Form extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Form")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(validated: js.UndefOr[Boolean] = js.undefined,
            inline: js.UndefOr[Boolean] = js.undefined): WithProps = auto
}
