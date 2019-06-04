package tripletail

import org.scalatest.{FunSuite, Matchers}

class IntegrationTest extends FunSuite with Matchers {
  val url = "http://127.0.0.1:7979/api/v1/tripletail/"
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  test("fault") {
  }
}