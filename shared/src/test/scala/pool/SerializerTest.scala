package pool

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SerializerTest extends AnyFunSuite with Matchers {
  import upickle.default._
  import Serializers._

  test("licensee") {
    val licensee = Licensee("test@test.com")
    val licenseeJson = write(licensee)
    println(licensee)
    println(licenseeJson)
    println(read[Licensee](licenseeJson))
    licensee shouldBe read[Licensee](licenseeJson)
  }

  test("pool") {
    val pool = Pool(license = "abc123", name = "pool", built = 1991, lat = 26.85, lon = 82.29, volume = 10000)
    val poolJson = write(pool)
    println(pool)
    println(poolJson)
    println(read[Pool](poolJson))
    pool shouldBe read[Pool](poolJson)
  }
}