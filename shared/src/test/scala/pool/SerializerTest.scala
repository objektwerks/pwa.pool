package pool

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SerializerTest extends AnyFunSuite with Matchers {
  import upickle.default._
  import Serializers._

  test("account") {
    val account = Account("test@test.com")
    val accountJson = write[Account](account)
    account shouldBe read[Account](accountJson)

    println(s"upickle account: $account")
    println(s"upickle account as json: $accountJson")
    println(s"upickle read account json: ${read[Account](accountJson)}")
  }

  test("pool") {
    val pool = Pool(license = "abc123", name = "pool", built = 1991, lat = 26.85, lon = 82.29, volume = 10000)
    val poolJson = write[Pool](pool)
    pool shouldBe read[Pool](poolJson)
    
    println(s"upickle pool: $pool")
    println(s"upickle pool as json: $poolJson")
    println(s"upickle read pool json: ${read[Pool](poolJson)}")
  }
}