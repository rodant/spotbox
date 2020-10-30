package me.spoter.services

import java.net.URI

import cats.implicits._
import me.spoter.models._
import me.spoter.models.rdf.IRI
import me.spoter.solid_libs._
import sttp.client._
import sttp.model.{HeaderNames, Uri}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResourceService {
  private val podServerUrl = "http://localhost:8080"
  implicit val sttpBackend: SttpBackend[Future, Nothing, NothingT] = FetchBackend()

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

  def getPods(webId: URI): List[IRI] = RDFHelper.getAll(webId, RDFHelper.PIM("storage")).map(p => IRI(p.value.toString)).toList

  def getSpotPodFromStore(user: User): Option[IRI] = getPods(user.webId).find(_.toString.startsWith(podServerUrl))

  def getSpotPod(user: User): Future[Option[IRI]] =
    for {
      result <- basicRequest.get(buildPodApiURI(user)).followRedirects(false).send()
        .map(_.header(HeaderNames.Location).map(IRI(_)))
    } yield result

  def deleteSpotPod(user: User): Future[Either[String, String]] = {
    val webIdRDF = RDFLib.sym(user.webId.toString)
    val uri = buildPodApiURI(user)
    for {
      resp <- basicRequest.delete(uri).send().map {
        case r if r.isSuccess =>
          val locationOpt = getSpotPodFromStore(user).map(_.toString) // workaround because the location header isn't there!
          locationOpt.toRight("Impossible code branch, found no spoter.ME POD!")
        case r if r.isClientError => Left(s"Client Error: ${r.code}")
        case r => Left(s"Server Error: ${r.code}")
      }
      result <- resp.map { podUri =>
        RDFHelper.delStatementFromWeb(RDFLib.st(webIdRDF, RDFHelper.PIM("storage"), RDFLib.sym(podUri), webIdRDF))
          .recoverWith { case e if e.getMessage.contains("Remote Ok") => Future(()) } // workaround, store bug
          .map(_ => podUri)
      }.bifoldMap(err => Future(Left(err)), _.map(Right(_)))
    } yield result
  }

  def addPodStatement(user: User, pod: IRI): Future[Unit] = {
    val webIdRDF = RDFLib.sym(user.webId.toString)
    RDFHelper.addStatementToWeb(RDFLib.st(webIdRDF, RDFHelper.PIM("storage"), RDFLib.sym(pod.toString), webIdRDF))
  }

  private def buildPodApiURI(user: User): Uri = {
    val encodedWebId = scalajs.js.URIUtils.encodeURIComponent(user.webId.toString)
    uri"$podServerUrl/management-api/pods/${user.webId}"
  }
}

