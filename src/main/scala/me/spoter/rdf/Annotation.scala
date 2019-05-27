package me.spoter.rdf

/**
  * RDF Annotation.
  */
sealed trait Annotation {
  val value: String
}

case class LangAnnotation(value: String) extends Annotation

case class TypeAnnotation(value: String) extends Annotation
