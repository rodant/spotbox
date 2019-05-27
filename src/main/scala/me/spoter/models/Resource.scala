package me.spoter.models

case class Resource(iri: IRI = IRI(""), name: String = "", folder: Boolean = false) extends SPOTEntity {
  override def withNewName(n: String): SPOTEntity = copy(name = n)
}
