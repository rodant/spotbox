package me.spoter.models

import java.net.URI

/**
  * Wrapper around the java.net.URI class.
  */
case class IRI(private val sourceUri: URI) {
  val innerUri: URI = sourceUri
  private val uriStr: String = innerUri.toString
  private val endOfBaseUri = uriStr.lastIndexOf("/")

  lazy val baseIRI: IRI = IRI(uriStr.substring(0, endOfBaseUri + 1))

  val lastPathComponent: String = uriStr.substring(endOfBaseUri + 1)

  def concatPath(path: String): IRI = IRI(s"$uriStr/$path")

  def removeTailingSlash: IRI = IRI {
    if (endOfBaseUri == uriStr.length - 1) uriStr.substring(0, endOfBaseUri)
    else uriStr
  }

  def parent: IRI = removeTailingSlash.baseIRI

  override def toString: String = innerUri.toString
}

object IRI {
  def apply(uriStr: String): IRI = IRI(URI.create(uriStr))
}
