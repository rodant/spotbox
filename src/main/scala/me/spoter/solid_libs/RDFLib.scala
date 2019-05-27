package me.spoter.solid_libs

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */
@js.native
@JSImport("rdflib", JSImport.Default)
object RDFLib extends js.Object {
  def Namespace(uri: String): js.Dynamic = js.native

  def graph(): js.Dynamic = js.native

  def sym(subject: String): js.Dynamic = js.native

  def st(subject: js.Dynamic, predicate: js.Dynamic, obj: js.Dynamic, doc: js.Dynamic): js.Dynamic = js.native

  def literal(value: String, lang: js.UndefOr[String] = js.undefined, typ: js.UndefOr[js.Dynamic] = js.undefined): js.Dynamic = js.native
}

@js.native
@JSImport("rdflib", "Fetcher")
class RDFFetcher(store: js.Dynamic) extends js.Object {
  def load(subject: String, options: js.UndefOr[js.Dynamic]): js.Promise[js.Object] = js.native

  def createContainer(parentURI: String, folderName: String, data: js.UndefOr[String]): js.Promise[js.Object] = js.native

  def webOperation(method: String, uri: String, options: js.Dynamic = js.Dynamic.literal()): js.Promise[js.Object] = js.native
}

@js.native
@JSImport("rdflib", "UpdateManager")
class RDFUpdateManager(store: js.Dynamic) extends js.Object {
  /**
    * Suitable for creating a new file resource. For containers @see RDFFetcher#createContainer.
    */
  def put(doc: js.Dynamic, data: js.Array[js.Dynamic], contentType: String, callback: js.Function): js.Promise[js.Object] = js.native

  def update(deletions: js.UndefOr[js.Array[js.Dynamic]], insertions: js.UndefOr[js.Array[js.Dynamic]], callback: js.Function): Unit = js.native

  def reloadAndSync(doc: js.Dynamic): Unit = js.native
}