package me.spoter.services

import me.spoter.models.{IRI, Resource}
import me.spoter.solid_libs.RDFHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResourceService {
  def listFolder(iri: IRI, showHidden: Boolean = false): Future[Seq[Resource]] = {
    RDFHelper.listDir(iri.innerUri)
      .flatMap { us =>
        Future.traverse(us) { u =>
          RDFHelper.loadEntity(u) {
            val iri = IRI(u)
            Resource(iri, iri.removeTailingSlash.lastPathComponent)
          }.recover {
            case e if e.getMessage.contains("Forbidden") => Resource()
          }
        }.map(_.filter(r => r != Resource.BlankResource && (if (showHidden) true else !r.isHidden)))
      }
  }
}

