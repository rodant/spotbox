package me.spoter.components.bootstrap

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithProps}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object BreadcrumbItem extends ReactBridgeComponent {

  @JSImport("react-bootstrap", "Breadcrumb.Item")
  @js.native
  object RawComponent extends js.Object

  override protected lazy val componentValue: js.Any = RawComponent

  def apply(active: js.UndefOr[Boolean] = js.undefined,
            href: js.UndefOr[String] = js.undefined,
            target: js.UndefOr[String] = js.undefined): WithProps = auto
}

