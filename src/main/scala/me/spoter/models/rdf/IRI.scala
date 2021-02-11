package me.spoter.models.rdf

import java.net.URI

import scala.annotation.tailrec

/**
  * Wrapper around the java.net.URI class.
  */
case class IRI(private val sourceUri: URI) {
  val innerUri: URI = sourceUri
  private val uriStr: String = innerUri.toString
  private val endOfBaseUri =
    if (!uriStr.endsWith("/") && !uriStr.endsWith(authority)) uriStr.lastIndexOf("/")
    else if (uriStr.endsWith("/") && !uriStr.dropRight(1).endsWith(authority)) uriStr.dropRight(1).lastIndexOf("/")
    else uriStr.length - 1

  lazy val authority: String = innerUri.getAuthority match {
    case null => "/"
    case a => a
  }

  lazy val root: IRI = IRI.root(this)

  lazy val parent: IRI = if (endOfBaseUri > -1) IRI(uriStr.substring(0, endOfBaseUri + 1)) else this

  lazy val lastPathComponent: String = removeTailingSlash(uriStr.substring(endOfBaseUri + 1))

  lazy val removedTailingSlash: IRI = if (uriStr.endsWith("/")) IRI(removeTailingSlash(uriStr)) else this

  private def removeTailingSlash(s: String): String = if (s.endsWith("/")) s.dropRight(1) else s

  def concatPath(path: String): IRI = IRI(s"${removeTailingSlash(uriStr)}/$path")

  def normalize: IRI = IRI(innerUri.normalize())

  override def toString: String = uriStr

  def shortString: String = if (parent != this) lastPathComponent else authority
}

object IRI {
  val BlankNodeIRI: IRI = IRI("_blank")

  def apply(uriStr: String): IRI = IRI(URI.create(uriStr))

  @tailrec
  def root(iri: IRI): IRI = iri.parent match {
    case p if p == iri => iri
    case p => root(p)
  }
}
