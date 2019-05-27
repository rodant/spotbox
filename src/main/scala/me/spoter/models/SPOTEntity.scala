package me.spoter.models

trait SPOTEntity {
  val iri: IRI
  val name: String

  def withNewName(n: String): SPOTEntity
}

object BlankNodeEntity extends SPOTEntity {
  override val iri: IRI = IRI.BlankNodeIRI
  override val name: String = "Blank Node"

  override def withNewName(n: String): SPOTEntity = this
}