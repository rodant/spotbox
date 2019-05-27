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

  def fetchUsers(): Future[Seq[User]] = findWebIds().flatMap(webIds => Future.sequence(webIds.map(fetchUser)))

  def findWebIds(): Future[Seq[URI]] = {
    val usersUriPattern = URI.create("https://spoterme.solid.community/data/users*")
    val platformUri = URI.create("https://spoterme.solid.community/profile/card#me")
    val affiliation = RDFHelper.SCHEMA_ORG("affiliation")
    RDFHelper.loadEntity(usersUriPattern) {
      RDFHelper.statementsMatching(None, Some(affiliation), Some(platformUri), None)
        .map(st => URI.create(st.subject.value.toString))
    }
  }
}
