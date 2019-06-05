package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object Breadcrumb extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Breadcrumb")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(bsPrefix: js.UndefOr[String] = js.undefined): WithProps = auto
}

