package me.spoter.solid_libs

import java.net.URI

import me.spoter.models.IRI
import me.spoter.rdf.RdfLiteral
import org.scalajs.dom.ext.Ajax.InputData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.UndefOr

/**
  * First draft of an abstraction over the RDFLib.
  */
// TODO: avoid dependency to JS, hint: use the RDF typescript defs.
object RDFHelper {
  val RDF: js.Dynamic = RDFLib.Namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
  val FOAF: js.Dynamic = RDFLib.Namespace("http://xmlns.com/foaf/0.1/")
  val GOOD_REL: js.Dynamic = RDFLib.Namespace("http://purl.org/goodrelations/v1#")
  val PROD: js.Dynamic = RDFLib.Namespace("http://www.productontology.org/id/")
  val SCHEMA_ORG: js.Dynamic = RDFLib.Namespace("http://schema.org/")
  val XMLS: js.Dynamic = RDFLib.Namespace("http://www.w3.org/2001/XMLSchema#")
  val LDP: js.Dynamic = RDFLib.Namespace("http://www.w3.org/ns/ldp#")
  val VCARD: js.Dynamic = RDFLib.Namespace("http://www.w3.org/2006/vcard/ns#")
  val PIM: js.Dynamic = RDFLib.Namespace("http://www.w3.org/ns/pim/space#")

  private val store = RDFLib.graph()
  private val fetcher = new RDFFetcher(store)
  private val updateManager = new RDFUpdateManager(store)

  def ensureContainerExists(iri: IRI): Future[Unit] =
    for {
      res <- loadResource(iri)
      _ <- res.fold(_ => createContainerResource(iri.baseIRI.innerUri, iri.lastPathComponent), Future(_))
    } yield ()

  def loadResource(iri: IRI, forceLoad: Boolean = false): Future[Either[Throwable, js.Dynamic]] = {
    val options: UndefOr[js.Dynamic] = if (forceLoad) js.Dynamic.literal(force = forceLoad) else js.undefined
    load(iri.innerUri, options).map { res =>
      val dynResult = res.asInstanceOf[js.Dynamic]
      if (res.hasOwnProperty("error")) {
        Left(new Exception(dynResult.status.toString))
      } else {
        Right(dynResult)
      }
    }.recover { case e => Left[Throwable, js.Dynamic](e) }
  }

  private def load(sub: URI, options: js.UndefOr[js.Dynamic] = js.undefined): Future[js.Object] =
    fetcher.load(sub.toString, options).toFuture

  /**
    * don't use @metaString, there is a bug in the rdflib: https://github.com/linkeddata/rdflib.js/issues/266
    */
  def createContainerResource(parentUri: URI, containerName: String, metaString: Option[String] = None): Future[js.Object] = {
    fetcher.createContainer(parentUri.toString, containerName, metaString.orUndefined).toFuture
  }

  def uploadFile(uri: IRI, data: InputData, contentType: String): Future[js.Object] = {
    fetcher.webOperation("PUT", uri.toString,
      js.Dynamic.literal(
        contentType = contentType,
        data = data
      )
    ).toFuture
  }

  def deleteResource(iri: IRI): Future[js.Object] = fetcher.webOperation("DELETE", iri.toString).toFuture

  def reloadAndSync(iri: IRI): Unit = updateManager.reloadAndSync(RDFLib.sym(iri.toString))

  def listDir(dirUri: URI, forceLoad: Boolean = false): Future[Seq[URI]] = RDFHelper.loadEntity[Seq[URI]](dirUri, forceLoad) {
    val filesNodes = RDFHelper.getAll(dirUri, RDFHelper.LDP("contains"))
    filesNodes.map(f => new URI(f.value.toString))
  }

  def loadEntity[A](sub: URI, forceLoad: Boolean = false)(b: => A): Future[A] = {
    val options: UndefOr[js.Dynamic] = if (forceLoad) js.Dynamic.literal(force = forceLoad) else js.undefined
    load(sub, options).map(_ => b)
  }

  private def getAll(sub: URI, prop: js.Dynamic): js.Array[js.Dynamic] =
    store.each(RDFLib.sym(sub.toString), prop).asInstanceOf[js.Array[js.Dynamic]]

  def get(sub: URI, prop: js.Dynamic): js.Dynamic = store.any(RDFLib.sym(sub.toString), prop)

  def createFileResource(sub: URI, data: Seq[js.Dynamic], callback: js.Function): Future[js.Object] = {
    updateManager.put(RDFLib.sym(sub.toString), data.toJSArray, "text/turtle", callback).toFuture
  }

  def addStatementToWeb(st: js.Dynamic): Future[Unit] = addStatementsToWeb(Seq(st))

  def addStatementsToWeb(sts: Seq[js.Dynamic]): Future[Unit] = doUpdate(Seq(), sts)

  def delStatementFromWeb(st: js.Dynamic): Future[Unit] = delStatementsFromWeb(Seq(st))

  def delStatementsFromWeb(sts: Seq[js.Dynamic]): Future[Unit] = doUpdate(sts, Seq())

  def updateStatement(previous: RdfLiteral, st: js.Dynamic): Future[Unit] = {
    val delSts = Seq(RDFLib.st(st.subject, st.predicate, previous.toJSRdfLiteral, st.why))
    doUpdate(delSts, Seq(st))
  }

  def statementsMatching(sub: Option[URI], prop: Option[js.Dynamic], obj: Option[URI], doc: Option[URI]): Seq[js.Dynamic] = {
    val subNode = sub.map(s => RDFLib.sym(s.toString)).orUndefined
    val objNode = obj.map(o => RDFLib.sym(o.toString)).orUndefined
    val propNode = prop.orUndefined
    val docNode = doc.map(d => RDFLib.sym(d.toString)).orUndefined
    store.`match`(subNode, propNode, objNode, docNode).asInstanceOf[js.Array[js.Dynamic]]
  }

  private def doUpdate(delSts: Seq[js.Dynamic], st: Seq[js.Dynamic]): Future[Unit] = {
    val p = Promise[Unit]()
    val callback = (uri: UndefOr[String], success: Boolean, error: UndefOr[String]) => {
      if (success) {
        p.success()
      } else {
        p.failure(new Exception(error.get))
      }
    }
    updateManager.update(js.Array(delSts: _*), js.Array(st: _*), callback)
    p.future
  }
}
