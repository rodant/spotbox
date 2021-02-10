package me.spoter.models

import me.spoter.models.rdf.IRI
import org.scalatest.funsuite.AnyFunSuite

class IRITest extends AnyFunSuite {

  test("removeConsecutiveSlashes") {
    assertResult(IRI("http://example/level0/level1"), "Error with slash repetition in path")(
      IRI("http://example/level0//level1").normalize)
  }

  test("parent") {
    val iri = IRI("http://auth/level0/level1")
    assertResult(IRI("http://auth/level0/"), "Wrong parent!")(iri.parent)
    assertResult(IRI("http://auth/"), "Wrong parent after 2 calls!")(iri.parent.parent)
    assertResult(IRI("http://auth/"), "Wrong parent after 3 calls!")(iri.parent.parent.parent)
    assertResult(IRI("http://auth"), "Wrong parent for a non tailing slash after authority!")(
      IRI("http://auth").parent)

    val iri2 = IRI("https://example.com:2000/level0/level1")
    assertResult(IRI("https://example.com:2000/level0/"), "Wrong parent!")(iri2.parent)
    assertResult(IRI("https://example.com:2000/"), "Wrong parent after 2 calls!")(iri2.parent.parent)
    assertResult(IRI("https://example.com:2000/"), "Wrong parent after 3 calls!")(iri2.parent.parent.parent)

    val path = IRI("/level0/level1")
    assertResult(IRI("/level0/"), "Wrong parent after 1 calls!")(path.parent)
    assertResult(IRI("/"), "Wrong parent after 2 calls!")(path.parent.parent)
    assertResult(IRI("/"), "Wrong parent after 3 calls!")(path.parent.parent.parent)

    assertResult(IRI("level0/"), "Wrong parent for a relative path!")(IRI("level0/level1").parent)
    assertResult(IRI("level0/"), "Wrong parent for a relative path!")(IRI("level0/").parent.parent)
  }

  test("testLastPathComponent") {
    val iri = IRI("https://example.com/level0")
    assertResult("level0", "Wrong in case of no tailing slash!")(
      iri.lastPathComponent)
    assertResult("level0", "Wrong in case of no tailing slash!")(
      IRI("https://example.com/level0/").lastPathComponent)
  }

  test("root") {
    val iri = IRI("https://example.com/level0")
    assertResult(IRI("https://example.com/"), "Wrong root for 1 level")(iri.root)

    assertResult(IRI("https://example.com/"), "Wrong root for 2 levels")(
      IRI("https://example.com/level0/level1").root)

    assertResult(IRI("example.com/"), "Wrong root for 2 levels")(
      IRI("example.com/level0/level1").root)

    assertResult(IRI("/"), "Wrong root for 2 levels")(IRI("/level0/level1").root)
    assertResult(IRI("level0/"), "Wrong root for 2 levels")(IRI("level0/level1").root)
  }
}
