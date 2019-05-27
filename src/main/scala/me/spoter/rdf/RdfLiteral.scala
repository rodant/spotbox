package me.spoter.rdf

import me.spoter.solid_libs.RDFLib

import scala.scalajs.js
import scala.scalajs.js.UndefOr

/**
  *
  */
case class RdfLiteral(value: String, lang: Option[LangAnnotation] = None, typ: js.UndefOr[js.Dynamic] = js.undefined) {
  def toJSRdfLiteral: js.Dynamic = RDFLib.literal(
    value,
    lang.fold[UndefOr[String]](js.undefined)(l => UndefOr.any2undefOrA(l.value)),
    typ)
}

object RdfLiteral {
  def fromJSRflLiteral(literal: js.Dynamic): RdfLiteral = {
    val lang = if (literal.lang.toString != "") Some(LangAnnotation(literal.lang.toString)) else None
    RdfLiteral(literal.value.toString, lang)
  }
}