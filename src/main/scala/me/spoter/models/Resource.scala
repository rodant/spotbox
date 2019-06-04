package me.spoter.models

case class Resource(iri: IRI = IRI.BlankNodeIRI, name: String = "", folder: Boolean = false) extends SPOTEntity {
  override def withNewName(n: String): SPOTEntity = copy(name = n)
}

object Resource {
  val BlankResource = Resource()
}
