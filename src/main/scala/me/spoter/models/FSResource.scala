package me.spoter.models

import me.spoter.models.rdf.IRI

/**
  * An ADT model for file system resources.
  */
sealed abstract class FSResource(val iri: IRI, val name: String) {
  lazy val isHidden: Boolean = name.startsWith(".")

  def withNewName(n: String): FSResource
}

object BlankNodeFSResource extends FSResource(IRI.BlankNodeIRI, IRI.BlankNodeIRI.lastPathComponent) {
  override lazy val isHidden: Boolean = true

  override def withNewName(n: String): FSResource = this
}

case class Folder(override val iri: IRI = BlankNodeFSResource.iri,
                  override val name: String = "") extends FSResource(iri, name) {
  override def withNewName(n: String): Folder = copy(name = n)
}

case class File(override val iri: IRI = BlankNodeFSResource.iri,
                override val name: String = "") extends FSResource(iri, name) {
  override def withNewName(n: String): FSResource = copy(name = n)
}
