package me.spoter.models

import java.net.URI

/**
  * User class.
  */
case class User(uri: URI, emailUri: Option[URI] = None)
