package me.spoter.services

import java.net.URI

import me.spoter.models.User
import me.spoter.solid_libs._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.Success

object UserService {
  def fetchUser(webId: URI): Future[User] = {
    RDFHelper.loadEntity(webId) {
      val pods = ResourceService.getPods(webId)
      val hasEmailNode = RDFHelper.get(webId, RDFHelper.VCARD("hasEmail"))
      hasEmailNode match {
        case n if js.isUndefined(n) => Future(User(webId, pods))
        case _ =>
          val emailUri = new URI(hasEmailNode.value.toString)
          RDFHelper.loadEntity(emailUri)(
            User(webId, pods, Some(new URI(RDFHelper.get(emailUri, RDFHelper.VCARD("value")).value.toString)))
          )
      }
    }.flatten
      .andThen {
        case Success(user) => ResourceService.getSpotPod(user).flatMap {
          case Some(sp) if !user.pods.exists(_.removedTailingSlash == sp.removedTailingSlash) =>
            ResourceService.addPodStatement(user, sp)
        }
      }
  }
}
