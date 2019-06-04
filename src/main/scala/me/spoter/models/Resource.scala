package me.spoter.models

case class Resource(iri: IRI = IRI.BlankNodeIRI,
                    name: String = IRI.BlankNodeIRI.lastPathComponent,
                    isFolder: Boolean = false) extends SPOTEntity {
  override def withNewName(n: String): Resource = copy(name = n)

  lazy val isHidden: Boolean = name.startsWith(".")
}

object Resource {
  val BlankResource = Resource()
}
