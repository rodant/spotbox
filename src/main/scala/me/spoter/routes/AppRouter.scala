package me.spoter.routes

import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import me.spoter.components.{Footer, TopNav}
import me.spoter.models.IRI
import me.spoter.pages.SPOTBox

object AppRouter {

  sealed trait AppPage

  case class Explorer(iri: String = IRI.BlankNodeIRI.toString) extends AppPage {
    lazy val dirIri: String = IRI(if (iri.endsWith("/")) iri else s"$iri/").normalize.toString
  }

  private val config = RouterConfigDsl[AppPage].buildConfig { dsl =>
    import dsl._

    (trimSlashes
      | staticRoute(root, Explorer()) ~> render(SPOTBox.Page(IRI.BlankNodeIRI.toString))
      | dynamicRouteCT[Explorer]("#explorer?iri=" ~ string(".+").caseClass[Explorer]) ~> dynRender(p => SPOTBox.Page(p.dirIri)))
      .notFound(redirectToPage(Explorer())(Redirect.Replace))
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
