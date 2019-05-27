package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */
object NavBarBrand extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Navbar.Brand")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(href: js.UndefOr[String] = js.undefined): WithProps = auto
}

