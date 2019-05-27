package me.spoter.solid_libs

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  *
  */
@js.native
@JSImport("solid-auth-client", JSImport.Default)
object SolidAuth extends js.Object {
  def popupLogin(popupData: js.Dynamic): js.Object = js.native

  def logout(): js.Object = js.native

  def trackSession(callback: js.Function1[js.Dynamic, Unit]): Unit = js.native

  def currentSession(): js.Promise[js.Dynamic] = js.native

  def addListener(event: String, callback: js.Function1[js.Dynamic, Unit]): Unit = js.native

  def removeListener(event: String, callback: js.Function1[js.Dynamic, Unit]): Unit = js.native

  def listenerCount(event:String): Int = js.native
}
