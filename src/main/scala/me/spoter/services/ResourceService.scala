package me.spoter.services

import cats.implicits._
import me.spoter.models._
import me.spoter.models.rdf.IRI
import me.spoter.solid_libs._
import org.scalajs.dom.experimental.Response
import sttp.client._
import sttp.model.{HeaderNames, Uri}

import java.net.URI
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Dynamic

object ResourceService {
  //private val podServerUrl = "https://spoterme-solid-server.herokuapp.com"
  private val podServerUrl = "http://localhost:8080"
  implicit val sttpBackend: SttpBackend[Future, Nothing, NothingT] = FetchBackend()

  private def resourceFrom(iri: IRI, res: Option[js.Object] = None, isPod: Boolean = false): Future[FSResource] = {
    val types = RDFHelper.getAll(iri.innerUri, RDFHelper.RDF("type"))
    val isContainer = types.exists(_.value.toString.contains("Container"))
    if (isContainer) Future.successful(Folder(iri, if (isPod) iri.removedTailingSlash.toString else iri.shortString))
    else {
      res.map {
        case r: Response =>
          val contentType = r.headers.get("Content-Type").getOrElse(File.defaultType)
          Future.successful(File(iri, iri.lastPathComponent, `type` = contentType))
      }.getOrElse(Future.successful(File(iri, iri.lastPathComponent)))
    }
  }

  def createFolder(iri: IRI, name: String): Future[Unit] =
    RDFHelper.createContainerResource(iri.innerUri, name).map(_ => ())

  def listFolder(iri: IRI, isWebId: Boolean = false, showHidden: Boolean = false, forceLoad: Boolean = false): Future[Seq[FSResource]] = {
    val property = if (isWebId) RDFHelper.PIM("storage") else RDFHelper.LDP("contains")
    RDFHelper.listDir(iri.innerUri, property, forceLoad)
      .flatMap { us =>
        us.toList.traverse { u =>
          RDFHelper.flatLoadEntity(u, forceLoad = forceLoad)(res => resourceFrom(IRI(u), Option(res), isPod = isWebId))
            .recover {
              case e if e.getMessage.contains("Forbidden") => BlankNodeFSResource
            }
        }.map(_.filter(r => r != BlankNodeFSResource && (if (showHidden) true else !r.isHidden)))
      }
  }

  def deleteResource(r: FSResource): Future[Unit] = {
    def delete(iri: IRI): Future[Unit] = RDFHelper.deleteResource(iri).map(_ => RDFHelper.reloadAndSync(iri.parent))

    r match {
      case BlankNodeFSResource => Future.successful(())
      case File(iri, _, _, _) => delete(iri)
      case Folder(iri, _) =>
        for {
          children <- RDFHelper.listDir(iri.innerUri, RDFHelper.LDP("contains"), forceLoad = true)
          frs <- children.toList.traverse(u => resourceFrom(IRI(u)))
          _ <- frs.traverse(fr => deleteResource(fr))
          _ <- delete(iri)
        } yield ()
    }
  }

  def isPod(iri: IRI): Boolean = {
    val pods = RDFHelper.statementsMatching(None, Some(RDFHelper.PIM("storage")), None, None)
    if (pods.nonEmpty) {
      pods.exists(p => IRI(p.`object`.value.toString).removedTailingSlash.toString == iri.removedTailingSlash.toString)
    }
    else {
      // for the case the user profile isn't loaded yet
      val pathComponents = iri.innerUri.getPath.split("/").toList
      val nonEmptyComponents = pathComponents.count(_.nonEmpty)
      nonEmptyComponents == 0 || (nonEmptyComponents == 1 && iri.toString.startsWith(podServerUrl))
    }
  }

  def getPods(webId: URI): List[IRI] = RDFHelper.getAll(webId, RDFHelper.PIM("storage")).map(p => IRI(p.value.toString)).toList

  def getSpotPodFromStore(user: User): Option[IRI] = getPods(user.webId).find(_.toString.startsWith(podServerUrl))

  def getSpotPod(user: User): Future[Option[IRI]] =
    for {
      res <- basicRequest.get(buildPodApiURI(user)).send()
    } yield res.header(HeaderNames.Location).map(IRI(_))

  def createSpotPod(user: User, podName: String): Future[Either[String, String]] = {
    val uri = buildPodApiURI(user)
    for {
      resp <- basicRequest.post(uri).body("podName" -> podName).send().flatMap {
        case r if r.isSuccess =>
          val maybeLoc = r.header(HeaderNames.Location)
            .fold[Either[String, String]](Left(s"Error: no ${HeaderNames.Location} header present"))(Right(_))
          maybeLoc.fold(
            _ => getSpotPod(user).map(oi => oi.toRight("Error: got no POD after creation!").map(_.toString)),
            loc => Future.successful(Right(loc)))
        case r if r.isClientError => Future.successful(Left(s"Client Error: ${r.code}"))
        case r => Future.successful(Left(s"Server Error: ${r.code}"))
      }.recoverWith { case e => Future.successful(Left(s"Server Error: ${e.getMessage}")) }
      result <- resp.map(podUri => addPodStatement(user, podUri).map(_ => podUri))
        .bifoldMap(err => Future.successful(Left(err)), _.map(Right(_)))
    } yield result
  }

  def deleteSpotPod(user: User): Future[Either[String, String]] = {
    val uri = buildPodApiURI(user)
    for {
      loc <- basicRequest.delete(uri).send().map {
        case r if r.isSuccess => r.header(HeaderNames.Location).toRight("Impossible code branch, location header!")
        case r if r.isClientError => Left(s"Client Error: ${r.code}")
        case r => Left(s"Server Error: ${r.code}")
      }
      result <- loc.map { podUri =>
        delPodStatement(user, podUri)
          .recoverWith { case e if e.getMessage.contains("Remote Ok") => Future(()) } // workaround, store bug
          .map(_ => podUri)
      }.bifoldMap(err => Future.successful(Left(err)), _.map(Right(_)))
    } yield result
  }

  def addPodStatement(user: User, podIRI: String): Future[Unit] = updatePodStatement(RDFHelper.addStatementToWeb, user, podIRI)

  def delPodStatement(user: User, podIRI: String): Future[Unit] = updatePodStatement(RDFHelper.delStatementFromWeb, user, podIRI)

  private def updatePodStatement(op: Dynamic => Future[Unit], user: User, podIri: String): Future[Unit] = {
    val webIdRDF = RDFLib.sym(user.webId.toString)
    op(RDFLib.st(webIdRDF, RDFHelper.PIM("storage"), RDFLib.sym(podIri), webIdRDF))
  }

  private def buildPodApiURI(user: User): Uri = uri"$podServerUrl/management-api/pods/${user.webId}"
}

