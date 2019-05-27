package me.spoter

import me.spoter.css.AppCSS
import me.spoter.routes.AppRouter
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExport

object ReactApp {

  @JSExport
  def main(args: Array[String]): Unit = {
    AppCSS.load()
    AppRouter.router().renderIntoDOM(dom.document.getElementById("spotbox-app"))
  }

}