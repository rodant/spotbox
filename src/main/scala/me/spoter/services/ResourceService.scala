package me.spoter.services

import me.spoter.models.{IRI, Resource}
import me.spoter.solid_libs.RDFHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.Success

object ResourceService {
  def createFolder(iri: IRI, name: String): Future[Unit] =
    RDFHelper.createContainerResource(iri.innerUri, name).map(_ => ())

  def listFolder(iri: IRI, showHidden: Boolean = false, forceLoad: Boolean = false): Future[Seq[Resource]] = {
    RDFHelper.listDir(iri.innerUri, forceLoad)
      .flatMap { us =>
        Future.traverse(us) { u =>
          RDFHelper.loadEntity(u) {
            val iri = IRI(u)
            val types = RDFHelper.getAll(u, RDFHelper.RDF("type"))
            val isContainer = types.exists(_.value.toString.contains("Container"))
            Resource(iri, iri.removedTailingSlash.lastPathComponent, isFolder = isContainer)
          }.recover {
            case e if e.getMessage.contains("Forbidden") => Resource()
          }
        }.map(_.filter(r => r != Resource.BlankResource && (if (showHidden) true else !r.isHidden)))
      }
  }

  def deleteResource(iri: IRI): Future[js.Object] = {
    RDFHelper.deleteResource(iri).andThen {
      case Success(res) =>
        RDFHelper.reloadAndSync(iri.parent)
        res
    }
  }
}

