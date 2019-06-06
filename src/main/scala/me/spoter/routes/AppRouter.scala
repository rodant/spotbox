package me.spoter.routes

import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import me.spoter.components.{Footer, TopNav}
import me.spoter.models.IRI
import me.spoter.pages.SPOTBox.Page

object AppRouter {

  sealed trait AppPage

  case object Home extends AppPage

  private val config = RouterConfigDsl[AppPage].buildConfig { dsl =>
    import dsl._

    (trimSlashes
      | staticRoute(root, Home) ~> render(Page(IRI.BlankNodeIRI.toString)))
      .notFound(redirectToPage(Home)(Redirect.Replace))
      .renderWith(layout)
  }

  private def layout(c: RouterCtl[AppPage], r: Resolution[AppPage]) =
    <.div(
      TopNav(),
      r.render(),
      Footer()
    )

  private val baseUrl = BaseUrl.fromWindowOrigin / ""

  val router = Router(baseUrl, config)
}
