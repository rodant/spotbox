package me.spoter.services

import java.net.URI

import me.spoter.models.User
import me.spoter.solid_libs.RDFHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js

object UserService {
  def fetchUser(userUri: URI): Future[User] = {
    RDFHelper.loadEntity(userUri) {
      val hasEmailNode = RDFHelper.get(userUri, RDFHelper.VCARD("hasEmail"))
      hasEmailNode match {
        case n if js.isUndefined(n) => Future(User(userUri))
        case _ =>
          val emailUri = new URI(hasEmailNode.value.toString)
          RDFHelper.loadEntity(emailUri)(
            User(userUri, Some(new URI(RDFHelper.get(emailUri, RDFHelper.VCARD("value")).value.toString)))
          )
      }
    }.flatten
  }
}
