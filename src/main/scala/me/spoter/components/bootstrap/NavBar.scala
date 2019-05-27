package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */
object NavBar extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Navbar")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(bg: js.UndefOr[String] = js.undefined,
            expand: js.UndefOr[String] = js.undefined,
            fixed: js.UndefOr[String] = js.undefined,
            variant: js.UndefOr[String] = js.undefined): WithProps = auto
}

