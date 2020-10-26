package me.spoter.services

import java.net.URI

import me.spoter.models.User
import me.spoter.models.rdf.IRI
import me.spoter.solid_libs.RDFHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js

object UserService {
  def fetchUser(userUri: URI): Future[User] = {
    RDFHelper.loadEntity(userUri) {
      val pods = RDFHelper.getAll(userUri, RDFHelper.PIM("storage")).map(p => IRI(p.value.toString)).toList
      val hasEmailNode = RDFHelper.get(userUri, RDFHelper.VCARD("hasEmail"))
      hasEmailNode match {
        case n if js.isUndefined(n) => Future(User(userUri, pods))
        case _ =>
          val emailUri = new URI(hasEmailNode.value.toString)
          RDFHelper.loadEntity(emailUri)(
            User(userUri, pods, Some(new URI(RDFHelper.get(emailUri, RDFHelper.VCARD("value")).value.toString)))
          )
      }
    }.flatten
  }
}
