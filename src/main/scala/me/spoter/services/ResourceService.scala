package me.spoter.services

import me.spoter.models.rdf.IRI
import me.spoter.models.{BlankNodeFSResource, File, Folder, FSResource}
import me.spoter.solid_libs.RDFHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.Success

object ResourceService {
  def createFolder(iri: IRI, name: String): Future[Unit] =
    RDFHelper.createContainerResource(iri.innerUri, name).map(_ => ())

  def listFolder(iri: IRI, showHidden: Boolean = false, forceLoad: Boolean = false): Future[Seq[FSResource]] = {
    RDFHelper.listDir(iri.innerUri, forceLoad)
      .flatMap { us =>
        Future.traverse(us) { u =>
          RDFHelper.loadEntity(u) {
            val iri = IRI(u)
            val types = RDFHelper.getAll(u, RDFHelper.RDF("type"))
            val isContainer = types.exists(_.value.toString.contains("Container"))
            if (isContainer) Folder(iri, iri.lastPathComponent)
            else File(iri, iri.lastPathComponent)
          }.recover {
            case e if e.getMessage.contains("Forbidden") => BlankNodeFSResource
          }
        }.map(_.filter(r => r != BlankNodeFSResource && (if (showHidden) true else !r.isHidden)))
      }
  }

  def deleteResource(r: FSResource): Future[js.Object] = {
    RDFHelper.deleteResource(r.iri).andThen {
      case Success(res) =>
        RDFHelper.reloadAndSync(r.iri.parent)
        res
    }
  }
}

