package me.spoter.models

import java.net.URI

import me.spoter.models.rdf.IRI

/**
  * User class.
  */
case class User(uri: URI, pods: List[IRI], emailUri: Option[URI] = None)
