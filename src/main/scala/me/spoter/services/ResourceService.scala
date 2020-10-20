package me.spoter.services

import cats.implicits._
import me.spoter.models.rdf.IRI
import me.spoter.models.{BlankNodeFSResource, FSResource, File, Folder}
import me.spoter.solid_libs.RDFHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResourceService {
  private def resourceFrom(iri: IRI, isPod: Boolean = false): FSResource = {
    val types = RDFHelper.getAll(iri.innerUri, RDFHelper.RDF("type"))
    val isContainer = types.exists(_.value.toString.contains("Container"))
    if (isContainer) Folder(iri, if (isPod) iri.removedTailingSlash.toString else iri.shortString)
    else File(iri, iri.lastPathComponent)
  }

  def createFolder(iri: IRI, name: String): Future[Unit] =
    RDFHelper.createContainerResource(iri.innerUri, name).map(_ => ())

  def listFolder(iri: IRI, isWebId: Boolean = false, showHidden: Boolean = false, forceLoad: Boolean = false): Future[Seq[FSResource]] = {
    val property = if (isWebId) RDFHelper.PIM("storage") else RDFHelper.LDP("contains")
    RDFHelper.listDir(iri.innerUri, property, forceLoad)
      .flatMap { us =>
        Future.traverse(us) { u =>
          RDFHelper.loadEntity(u)(resourceFrom(IRI(u), isPod = isWebId)).recover {
            case e if e.getMessage.contains("Forbidden") => BlankNodeFSResource
          }
        }.map(_.filter(r => r != BlankNodeFSResource && (if (showHidden) true else !r.isHidden)))
      }
  }

  def deleteResource(r: FSResource): Future[Unit] = {
    def delete(iri: IRI): Future[Unit] = RDFHelper.deleteResource(iri).map(_ => RDFHelper.reloadAndSync(iri.parent))

    r match {
      case BlankNodeFSResource => Future(())
      case File(iri, _) => delete(iri)
      case Folder(iri, _) =>
        for {
          children <- RDFHelper.listDir(iri.innerUri, RDFHelper.LDP("contains"), forceLoad = true)
          _ <- children.toList.traverse(u => deleteResource(resourceFrom(IRI(u))))
          _ <- delete(iri)
        } yield ()
    }
  }

  def isPod(iri: IRI): Boolean = {
    val pods = RDFHelper.statementsMatching(None, Some(RDFHelper.PIM("storage")), None, None)
    pods.exists(p => p.`object`.value.toString == iri.toString || p.`object`.value.toString == iri.removedTailingSlash.toString)
  }
}

