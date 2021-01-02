package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */
object FormCheckInput extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "FormCheck.Input")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(id: js.UndefOr[String] = js.undefined,
            isInvalid: js.UndefOr[Boolean] = js.undefined,
            isValid: js.UndefOr[Boolean] = js.undefined,
            isStatic: js.UndefOr[Boolean] = js.undefined,
            `type`: js.UndefOr[String] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

