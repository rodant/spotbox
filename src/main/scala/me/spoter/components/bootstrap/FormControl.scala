package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}
import japgolly.scalajs.react.{Callback, ReactEventFromInput}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */

object FormControl extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Form.Control")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(as: js.UndefOr[String] = js.undefined,
            `type`: js.UndefOr[String] = js.undefined,
            defaultValue: js.UndefOr[String] = js.undefined,
            value: js.UndefOr[String] = js.undefined,
            readOnly: js.UndefOr[Boolean] = js.undefined,
            plaintext: js.UndefOr[Boolean] = js.undefined,
            rows: js.UndefOr[Int] = js.undefined,
            size: js.UndefOr[String] = js.undefined,
            onChange: js.UndefOr[ReactEventFromInput => Callback] = js.undefined): WithProps = auto

}
