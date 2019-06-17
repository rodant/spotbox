package me.spoter.services

import cats.implicits._
import me.spoter.models.rdf.IRI
import me.spoter.models.{BlankNodeFSResource, FSResource, File, Folder}
import me.spoter.solid_libs.RDFHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResourceService {
  private def resourceFrom(iri: IRI): FSResource = {
    val types = RDFHelper.getAll(iri.innerUri, RDFHelper.RDF("type"))
    val isContainer = types.exists(_.value.toString.contains("Container"))
    if (isContainer) Folder(iri, iri.lastPathComponent)
    else File(iri, iri.lastPathComponent)
  }

  def createFolder(iri: IRI, name: String): Future[Unit] =
    RDFHelper.createContainerResource(iri.innerUri, name).map(_ => ())

  def listFolder(iri: IRI, showHidden: Boolean = false, forceLoad: Boolean = false): Future[Seq[FSResource]] = {
    RDFHelper.listDir(iri.innerUri, forceLoad)
      .flatMap { us =>
        Future.traverse(us) { u =>
          RDFHelper.loadEntity(u)(resourceFrom(IRI(u))).recover {
            case e if e.getMessage.contains("Forbidden") => BlankNodeFSResource
          }
        }.map(_.filter(r => r != BlankNodeFSResource && (if (showHidden) true else !r.isHidden)))
      }
  }

  def deleteResource(r: FSResource): Future[Unit] = {
    def delete(iri: IRI): Future[Unit] = RDFHelper.deleteResource(iri).map(_ => RDFHelper.reloadAndSync(iri.parent))

    r match {
      case BlankNodeFSResource => Future()
      case File(iri, _) => delete(iri)
      case Folder(iri, _) =>
        for {
          children <- RDFHelper.listDir(iri.innerUri, forceLoad = true)
          _ <- children.toList.traverse(u => deleteResource(resourceFrom(IRI(u))))
          _ <- delete(iri)
        } yield ()
    }
  }
}

