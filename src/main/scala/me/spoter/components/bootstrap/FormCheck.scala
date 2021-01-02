package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}
import japgolly.scalajs.react.raw.React.Ref
import org.scalajs.dom.Node

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */
object FormCheck extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Form.Check")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(ref: js.UndefOr[Ref] = js.undefined,
            disabled: js.UndefOr[Boolean] = js.undefined,
            feedback: js.UndefOr[String] = js.undefined,
            feedbackTooltip: js.UndefOr[Boolean] = js.undefined,
            id: js.UndefOr[String] = js.undefined,
            inline: js.UndefOr[Boolean] = js.undefined,
            isInvalid: js.UndefOr[Boolean] = js.undefined,
            isValid: js.UndefOr[Boolean] = js.undefined,
            label: js.UndefOr[Node] = js.undefined,
            title: js.UndefOr[String] = js.undefined,
            `type`: js.UndefOr[String] = js.undefined,
            bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

