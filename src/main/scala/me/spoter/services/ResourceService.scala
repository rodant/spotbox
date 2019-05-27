package me.spoter.services

import me.spoter.models.{IRI, Resource}
import me.spoter.solid_libs.RDFHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  *
  */
object ResourceService {
  def listFolder(iri: IRI): Future[Seq[Resource]] = {
    RDFHelper.listDir(iri.innerUri)
      .flatMap { us =>
        Future.traverse(us) { u =>
          if (!u.toString.endsWith("robots.txt") && !u.toString.endsWith("favicon.ico")) {
            RDFHelper.loadEntity(u) {
              val iri = IRI(u)
              Resource(iri, iri.removeTailingSlash.lastPathComponent)
            }
          } else {
            Future.successful(Resource(IRI.BlankNodeIRI))
          }
        }
      }
  }
}
