package pool

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SerializerTest extends AnyFunSuite with Matchers {
  import upickle.default._
  import Serializers._

  test("account") {
    val account = Account("test@test.com")
    val accountJson = write[Account](account)
    println(account)
    println(accountJson)
    println(read[Account](accountJson))
    account shouldBe read[Account](accountJson)
  }

  test("pool") {
    val pool = Pool(license = "abc123", name = "pool", built = 1991, lat = 26.85, lon = 82.29, volume = 10000)
    val poolJson = write[Pool](pool)
    println(pool)
    println(poolJson)
    println(read[Pool](poolJson))
    pool shouldBe read[Pool](poolJson)
  }
}