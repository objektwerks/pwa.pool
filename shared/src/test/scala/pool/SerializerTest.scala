package pool

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SerializerTest extends AnyFunSuite with Matchers {
  import upickle.default._
  import Serializers._

  test("licensee") {
    val licensee = Licensee("test@test.com")
    val licenseeAsJson = write(licensee)
    println(licensee)
    println(licenseeAsJson)
    licensee shouldBe read[Licensee](licenseeAsJson)
  }
}