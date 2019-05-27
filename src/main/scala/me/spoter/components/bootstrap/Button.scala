package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}
import japgolly.scalajs.react._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */

object Button extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Button")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(variant: js.UndefOr[String] = js.undefined,
            `type`: js.UndefOr[String] = js.undefined,
            size: js.UndefOr[String] = js.undefined,
            href: js.UndefOr[String] = js.undefined,
            active: js.UndefOr[Boolean] = js.undefined,
            disabled: js.UndefOr[Boolean] = js.undefined,
            onClick: js.UndefOr[ReactEventFromInput => Callback] = js.undefined): WithProps = auto
}

