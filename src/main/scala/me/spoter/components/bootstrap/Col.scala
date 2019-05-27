package me.spoter.components.bootstrap

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{Children, JsComponent}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */

object Col {

  @JSImport("react-bootstrap", "Col")
  @js.native
  object RawComponent extends js.Object

  @js.native
  trait Props extends js.Object {
    var xl: js.Any = js.native
    var lg: js.Any = js.native
    var md: js.Any = js.native
    var sm: js.Any = js.native
    var xs: js.Any = js.native
  }

  private def props(xl: js.Any, lg: js.Any, md: js.Any, sm: js.Any, xs: js.Any): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.xl = xl
    p.lg = lg
    p.md = md
    p.sm = sm
    p.xs = xs
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](RawComponent)

  /**
    *
    * @param xl       true | auto | 1 |...| 12
    * @param lg       true | auto | 1 |...| 12
    * @param md       true | auto | 1 |...| 12
    * @param sm       true | auto | 1 |...| 12
    * @param xs       true | auto | 1 |...| 12
    * @param children the children components
    * @return a Vdom object
    */
  def apply(xl: js.Any = true, lg: js.Any = true, md: js.Any = true, sm: js.Any = true, xs: js.Any = true)
           (children: ChildArg*): VdomElement = component(props(xl, lg, md, sm, xs))(children: _*).vdomElement
}
